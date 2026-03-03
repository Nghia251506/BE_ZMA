package com.example.demo.Controller;

import com.example.demo.Dto.Auth.*;
import com.example.demo.Service.Auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. Dành cho Zalo Mini App (Chỉ lấy Token, không cần Cookie)
    @PostMapping("/login/zalo")
    public ResponseEntity<AuthResponse> loginZalo(@Valid @RequestBody LoginZaloRequest request) {

        AuthResponse response = authService.loginZalo(request);
        return ResponseEntity.ok(response);
    }

    // 2. Dành cho Admin/Quản lý trên Web (React/Next.js - Có dùng Cookie)
    @PostMapping("/login/basic")
    public ResponseEntity<AuthResponse> loginBasic(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.loginBasic(
                loginRequest,
                response));
    }

    // 3. Dành cho Khách hàng vãng lai đăng ký trên Next.js
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // 4. Lấy thông tin cá nhân (Dùng chung cho tất cả)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me() {
        return ResponseEntity.ok(authService.getMe());
    }

    // 5. Đăng xuất (Xóa cả SecurityContext và Cookie nếu có)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("Đã đăng xuất thành công");
    }

//    @PostMapping("/setup-admin")
//    public ResponseEntity<String> setupAdmin() {
//        authService.createDefaultAdmin();
//        return ResponseEntity.ok("Đã tạo tài khoản admin: admin/123456");
//    }
}