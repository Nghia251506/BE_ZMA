package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data // Tự động tạo getter/setter từ Lombok
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- NHÓM 1: ZALO INTEGRATION ---
    @Column(unique = true)
    private String zaloId; // ID định danh từ Zalo SDK
    private String avatarZalo;
    private String username;
    private String password;
    @Column(name = "verify_token")
    private String verifyToken;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // --- NHÓM 2: THÔNG TIN TỪ APP SHEET (Hồ sơ nhân sự) ---
    @Column(unique = true)
    private String maNV; // Mã nhân viên
    private String fullName;
    private String nhomKH; // Nhóm khách hàng
    private String workingType; // Part-time / Full-time
    private String position; // Vị trí
    private boolean isActive; // Tình trạng làm việc
    private Double positionCoefficient; // Hệ số chức vụ
    private String email;
    private LocalDate joinDate; // Ngày vào làm
    private LocalDate probationEndDate; // Ngày hết thử việc
    private String contractType; // Loại hợp đồng
    private String contractDuration; // Thời hạn HDLD
    private LocalDate birthDate;
    private String mainPhone;
    private String subPhone;
    private String homeTown; // Quê quán
    private String currentAddress; // Nơi ở hiện tại
    private String facebookLink;

    // --- NHÓM 3: HÌNH ẢNH & TÀI CHÍNH (Lưu URL ảnh) ---
    private String cccdFrontImg; // Ảnh CCCD mặt trước
    private String cccdBackImg;
    private String cardVisitImg;
    private String portraitImg; // Ảnh chân dung
    private String bankAccountNumber; // STK
    private String bankName;
    private String teamName;
    private Double basicSalary; // Lương cơ bản
    private boolean hasInsurance; // Bảo hiểm (true/false)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_direct_permissions", // Bảng trung gian cho quyền gán trực tiếp
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Dùng Set để tự động loại bỏ các quyền trùng lặp
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // 1. Lấy quyền từ Role (role_permissions)
        if (this.role != null && this.role.getPermissions() != null) {
            this.role.getPermissions().forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getName()))
            );
            // Thêm chính cái Role đó với prefix ROLE_ (Dùng cho hasRole trong SecurityConfig)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.getName()));
        }

        // 2. Lấy thêm quyền đặc cách được gán trực tiếp (user_permissions)
        if (this.permissions != null) {
            this.permissions.forEach(p ->
                    authorities.add(new SimpleGrantedAuthority(p.getName()))
            );
        }

        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() { return this.password; }

    // Các hàm này trả về true và không liên quan tới DB
    @Override
    @Transient // Báo cho Hibernate: "Đừng có tìm cột này trong DB"
    @JsonIgnore
    public boolean isAccountNonExpired() { return true; }

    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonLocked() { return true; }

    @Override
    @Transient
    @JsonIgnore
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEnabled() { return this.isActive; }
}
