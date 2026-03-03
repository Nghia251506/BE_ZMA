package com.example.demo.Service.User;

import com.example.demo.Dto.Auth.UserResponseDTO;
import com.example.demo.Dto.User.ChangePasswordRequestDTO;
import com.example.demo.Dto.User.UserCreateRequest;
import com.example.demo.Dto.User.UserRequestDTO;
import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserStatus;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository; // Giả định ông đã có
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    // --- 1. CRUD CƠ BẢN ---

    // Create: Admin tạo user ở trạng thái PENDING
    public UserResponseDTO createPendingUser(UserCreateRequest dto) {
        // 1. Kiểm tra username tồn tại
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        // 2. Map các thông tin cơ bản từ DTO sang Entity
        // (Lưu ý: Mapper lúc này đang ignore trường role nên user.getRole() đang null)
        User user = userMapper.toEntity(dto);

        // 3. XỬ LÝ ROLE: Tìm Role từ DB dựa trên chuỗi String (ADMIN, STAFF,...)
        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            Role role = roleRepository.findByName(dto.getRole())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Role '" + dto.getRole() + "' không tồn tại!"));

            // Gán nguyên Object Role vào User. Hibernate sẽ tự map role_id.
            user.setRole(role);
        }

        // 4. Thiết lập các thông số mặc định và bảo mật
        String verifyToken = UUID.randomUUID().toString();
        user.setVerifyToken(verifyToken);
        user.setStatus(UserStatus.PENDING);
        user.setActive(false);

        // Gán thủ công nếu Mapper đang ignore (như ông đang viết)
        user.setMainPhone(dto.getMainPhone());
        user.setSubPhone(dto.getSubPhone());

        // Mã hóa mật khẩu mặc định
        user.setPassword(passwordEncoder.encode("123456"));

        // 5. Lưu vào Database
        User savedUser = userRepository.save(user);

        // 6. Trả về DTO kèm Activation Link để Admin gửi Zalo
        UserResponseDTO response = userMapper.toDto(savedUser);

        // Thay ID app thật của ông vào đây nhé
        String miniAppId = "4598372927419253507";
        String activationLink = "https://zalo.me/s/" + miniAppId + "/?token=" + verifyToken;
        response.setActivationLink(activationLink);

        return response;
    }

    // Read: Lấy danh sách hoặc chi tiết
    public Page<UserResponseDTO> getUsersPageByRoles(List<String> roles, Pageable pageable) {
        return userRepository.findByRole_NameIn(roles, pageable)
                .map(userMapper::toDto);
    }

    // Update: Cập nhật thông tin cơ bản
    public UserResponseDTO updateUser(String maNV, UserRequestDTO dto) {
        User user = userRepository.findByMaNV(maNV)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        userMapper.updateEntityFromDto(dto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    // --- 2. HÀM DỪNG HỢP TÁC (Deactivate) ---
    public void stopCooperation(String maNV) {
        User user = userRepository.findByMaNV(maNV)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        user.setActive(false);
        user.setStatus(UserStatus.INACTIVE); // Đổi status để chặn login
        userRepository.save(user);
    }

    // --- 3. HÀM CẤP QUYỀN (Many-to-Many) ---
    public void grantPermissions(String maNV, Set<Long> permissionIds) {
        User user = userRepository.findByMaNV(maNV)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        user.setPermissions(new HashSet<>(permissions)); // Cập nhật bảng trung gian

        userRepository.save(user);
    }

    // --- 4. HÀM VERIFY VÀ BIND ZALO ---
    public UserResponseDTO verifyAndBindZalo(String token, String zaloId, String avatarZalo) {
        // Tìm user cầm token này
        User user = userRepository.findByVerifyToken(token)
                .orElseThrow(() -> new RuntimeException("Mã kích hoạt không hợp lệ hoặc đã hết hạn"));

        // Kiểm tra xem Zalo ID này đã bị ai chiếm chưa
        userRepository.findByZaloId(zaloId).ifPresent(u -> {
            if (!u.getMaNV().equals(user.getMaNV())) {
                throw new RuntimeException("Tài khoản Zalo này đã được liên kết với nhân viên khác!");
            }
        });

        // "Xe duyên" Zalo ID vào User hệ thống
        user.setZaloId(zaloId);
        user.setAvatarZalo(avatarZalo);
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);
        user.setVerifyToken(null); // Xóa token sau khi dùng xong để bảo mật

        return userMapper.toDto(userRepository.save(user));
    }

    public void changePassword(String username, ChangePasswordRequestDTO dto) {
        // 1. Tìm user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 2. Kiểm tra mật khẩu cũ (Dùng passwordEncoder.matches)
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        // 3. Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp nhau không
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // 4. Kiểm tra mật khẩu mới không được trùng mật khẩu cũ (Option bảo mật thêm)
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng mật khẩu cũ");
        }

        // 5. Mã hóa mật khẩu mới và lưu
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
