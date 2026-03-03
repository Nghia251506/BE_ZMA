package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Liên kết tài khoản đăng nhập của Khách (nếu có)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // --- THÔNG TIN CƠ BẢN ---
    private String fullName;
    private String email;
    private String phone;
    private String customerCode;
    private String address;
    private String mainPhone;
    private String subPhone;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private String zaloGroup;
    private String company;
    private String taxCode;

    // 2. Nhóm khách hàng (Tham chiếu đến User, lấy maNV khi ra DTO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_user_id") // Đổi tên cột để không trùng với user_id ở trên
    private User customerGroupUser;

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- CÁC TRƯỜNG QUẢN LÝ ---

    // 3. Người phụ trách (Assignee) - Nhân viên chăm sóc khách này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    // 4. Người tạo (Creator) - Admin hoặc Sale tạo bản ghi này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    // 5. Ngày tạo
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}