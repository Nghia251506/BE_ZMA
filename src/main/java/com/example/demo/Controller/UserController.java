package com.example.demo.Controller;

import com.example.demo.Dto.Auth.UserResponseDTO;
import com.example.demo.Dto.User.ChangePasswordRequestDTO;
import com.example.demo.Dto.User.UserCreateRequest;
import com.example.demo.Dto.User.UserRequestDTO;
import com.example.demo.Service.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    // --- NHÓM API CHO ADMIN QUẢN LÝ ---

    // 1. Lấy danh sách toàn bộ nhân viên
    /**
     * API 1: Lấy danh sách nhân viên nội bộ (ADMIN, STAFF)
     * URL: /api/users/internal?page=0&size=10&sort=maNV,desc
     */
    @GetMapping("/internal")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<Page<UserResponseDTO>> getInternalUsers(
            @PageableDefault(size = 10, sort = "maNV") Pageable pageable) {

        List<String> internalRoles = List.of("ADMIN", "STAFF");
        Page<UserResponseDTO> page = userService.getUsersPageByRoles(internalRoles, pageable);

        return ResponseEntity.ok(page);
    }

    /**
     * API 2: Lấy danh sách khách hàng (CUSTOMER)
     * URL: /api/users/customers?page=0&size=20
     */
    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<Page<UserResponseDTO>> getCustomers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {

        List<String> customerRoles = List.of("CUSTOMER");
        Page<UserResponseDTO> page = userService.getUsersPageByRoles(customerRoles, pageable);

        return ResponseEntity.ok(page);
    }

    // 2. Tạo nhân viên mới (Trạng thái PENDING + Gen Token)
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserResponseDTO> createPendingUser(@RequestBody UserCreateRequest dto) {
        return ResponseEntity.ok(userService.createPendingUser(dto));
    }

    // 3. Cập nhật thông tin nhân viên (Hồ sơ, ngân hàng, ảnh...)
    @PutMapping("/{maNV}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String maNV,
            @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(maNV, dto));
    }

    // 4. Dừng hợp tác (Deactivate user)
    @PatchMapping("/{maNV}/stop")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<String> stopCooperation(@PathVariable String maNV) {
        userService.stopCooperation(maNV);
        return ResponseEntity.ok("Đã dừng hợp tác với nhân viên " + maNV);
    }

    // 5. Cấp quyền trực tiếp cho User (Many-to-Many)
    @PostMapping("/{maNV}/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<String> grantPermissions(
            @PathVariable String maNV,
            @RequestBody Set<Long> permissionIds) {
        userService.grantPermissions(maNV, permissionIds);
        return ResponseEntity.ok("Cập nhật quyền thành công cho nhân viên " + maNV);
    }


    // --- NHÓM API CHO ZALO MINI APP ---

    // 6. API Kích hoạt tài khoản (Bind Zalo ID)
    // Mini App sẽ gọi API này khi nhân viên nhấn vào link có token
    @PostMapping("/verify-zalo")
    public ResponseEntity<UserResponseDTO> verifyZalo(
            @RequestParam String token,
            @RequestParam String zaloId,
            @RequestParam(required = false) String avatarZalo) {

        // Log một cái để test cho sướng ông ạ
        System.out.println("Kích hoạt Zalo cho Token: " + token + " với ZaloId: " + zaloId);

        UserResponseDTO result = userService.verifyAndBindZalo(token, zaloId, avatarZalo);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO dto,
            Principal principal) { // Principal lấy từ Security Context

        String username = principal.getName();
        userService.changePassword(username, dto);

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}
