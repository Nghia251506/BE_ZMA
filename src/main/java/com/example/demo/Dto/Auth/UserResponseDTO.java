package com.example.demo.Dto.Auth;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor  // Thêm cái này
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String maNV;
    private String fullName;
    private String position;
    private String avatarZalo;
    private String teamName;
    private String role; // Chỉ trả về tên Role
    private Set<String> permissions; // Trả về list quyền để FE ẩn/hiện button
    private String zaloId;   // null nếu chưa kích hoạt
    private String status;   // PENDING / ACTIVE
    private boolean active;  // helper field để FE check nhanh
    private String mainPhone;
    private String subPhone;
    private String activationLink;
}
