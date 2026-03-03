package com.example.demo.Service.Auth;

import com.example.demo.Dto.Auth.*;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder  passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse loginZalo(LoginZaloRequest request) {
        // 1. Tìm user theo zaloId
        User user = userRepository.findByZaloId(request.getZaloId())
                .orElseThrow(() -> new RuntimeException("Tài khoản Zalo này chưa được liên kết. Vui lòng kích hoạt qua link của Admin!"));

        // 3. Gen JWT Token từ thông tin User (Username, Roles, v.v.)
        String token = jwtTokenProvider.generateToken(user);

        log.info("Zalo Login: User {} ({}) đăng nhập thành công", user.getUsername(), user.getZaloId());

        return AuthResponse.builder()
                .accessToken(token)
                .user(userMapper.toDto(user))
                .build();
    }

    // --- 2. LOGIN BASIC (Cho Web Admin): DÙNG Cookie ---
    @Transactional(readOnly = true)
    public AuthResponse loginBasic(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai tài khoản hoặc mật khẩu!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai tài khoản hoặc mật khẩu!");
        }

        String token = jwtTokenProvider.generateToken(user);

        // Đóng dấu Cookie cho trình duyệt
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "");
        response.addCookie(cookie);

        return AuthResponse.builder()
                .accessToken(token)
                .user(userMapper.toDto(user))
                .build();
    }

    // --- LOGOUT: Xóa cả context và Cookie ---
    public void logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional
    public UserResponseDTO register(RegisterRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        // 2. Lấy Role mặc định cho khách hàng (CUSTOMER)
        // Ông nhớ đã chạy DataInitializer để có Role này trong DB nhé
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy quyền khách hàng!"));

        // 3. Tạo Entity User mới
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Bắt buộc mã hóa
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(customerRole)
                .isActive(true) // Đăng ký xong cho dùng luôn
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Khách hàng mới đăng ký thành công: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getMe() {
        // 1. Lấy thông tin từ Context (Đã được nạp ở Filter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal().toString())) {
            throw new RuntimeException("Chưa đăng nhập hoặc phiên làm việc hết hạn");
        }

        Object principal = auth.getPrincipal();

        // 2. Kiểm tra danh tính dựa trên Instance (Không cần Query DB lại)
//        if (principal instanceof org.example.event_platform.Entity.AdminPlatform admin) {
//            return adminPlatformMapper.toResponse(admin);
//        }

        if (principal instanceof com.example.demo.Entity.User user) {
            return userMapper.toDto(user);
        }

        // Trường hợp dự phòng nếu principal chỉ là username (String)
        throw new RuntimeException("Dữ liệu danh tính không hợp lệ trong Security Context");
    }

//    @Transactional
//    public void createDefaultAdmin() {
//        // 1. Kiểm tra xem đã có admin chưa để tránh tạo trùng
//        if (userRepository.existsByUsername("admin")) {
//            throw new RuntimeException("Tài khoản admin đã tồn tại rồi!");
//        }
//
//        // 2. Lấy Role ADMIN (Phải đảm bảo DataInitializer đã chạy và tạo Role này)
//        Role adminRole = roleRepository.findByName("ADMIN")
//                .orElseThrow(() -> new RuntimeException("Chưa có Role ADMIN trong DB. Hãy chạy DataInitializer trước!"));
//
//        // 3. Tạo User Admin
//        User admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("123456")) // Mật khẩu là 123456
//                .fullName("System Admin")
//                .email("admin@laptophn.vn")
//                .role(adminRole)
//                .isActive(true)
//                .build();
//
//        userRepository.save(admin);
//        log.info("--- Đã khởi tạo thành công tài khoản ADMIN tạm thời ---");
//    }



    private String verifyZaloToken(String token) {
        return "4598372927419253507"; // Hardcode ID của ông
    }
}
