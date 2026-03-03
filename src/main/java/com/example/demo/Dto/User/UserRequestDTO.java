package com.example.demo.Dto.User;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    // Thông tin cơ bản
    private String fullName;
    private String avatarZalo; // URL ảnh từ Zalo hoặc Admin upload
    private String subPhone;   // Số điện thoại phụ
    private LocalDate birthDate; // Dùng LocalDate để quản lý ngày sinh chuẩn

    // Thông tin công việc & Định danh
    private String position;
    private String teamName;

    // Thông tin tài chính & Marketing
    private String cartvisitImage;    // Link ảnh danh thiếp (Card Visit)
    private String bankAccountNumber; // Số tài khoản ngân hàng
    private String bankName;          // Tên ngân hàng (nên dùng Enum hoặc String)

    // (Tùy chọn) Ghi chú hoặc địa chỉ nếu sau này ông cần
    private String address;
}
