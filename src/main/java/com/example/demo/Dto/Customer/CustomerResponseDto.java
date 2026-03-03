package com.example.demo.Dto.Customer;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor // Cái này tạo ra constructor 11 tham số cho ông
@NoArgsConstructor
public class CustomerResponseDto {
    private Long id;            // 1
    private String customerCode; // 2
    private String fullName;     // 3 - Chuyển lên đây cho khớp COALESCE(c.fullName, ...)
    private String mainPhone;    // 4 - Khớp c.mainPhone
    private String subPhone;     // 5 - Khớp c.subPhone
    private String email;        // 6 - Khớp COALESCE(c.email, ...)
    private String customerGroup;// 7 - Khớp cg.maNV
    private String assigneeName; // 8 - Khớp ans.fullName
    private String creatorName;  // 9 - Khớp crt.fullName
    private LocalDateTime createdAt; // 10
    private String company;      // 11
    private String address;      // 12
}