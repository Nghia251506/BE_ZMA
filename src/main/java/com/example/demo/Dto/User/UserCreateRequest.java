package com.example.demo.Dto.User;
import lombok.*;
@Data
@Builder
@NoArgsConstructor  // <--- THÊM DÒNG NÀY ĐỂ JACKSON CÓ THỂ KHỞI TẠO OBJECT
@AllArgsConstructor
public class UserCreateRequest {
    private String maNV;      // Admin tự nhập hoặc hệ thống tự gen
    private String username;
    private String fullName;
    private String mainPhone;
    private String subPhone;
    private String email;
    private String position;
    private String teamName;
    private String role;      // Admin chọn STAFF hoặc MANAGER...
}
