package com.example.demo.Repository;

import com.example.demo.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByZaloId(String zaloId);
    Optional<User> findByUsername(String username);

    // 1. Tìm User bằng Verify Token để kích hoạt tài khoản
    Optional<User> findByVerifyToken(String verifyToken);

    // 2. Tìm theo mã nhân viên (Vì DTO của ông dùng maNV)
    Optional<User> findByMaNV(String maNV);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Check trùng mã nhân viên khi Admin tạo mới
    boolean existsByMaNV(String maNV);

    // Phục vụ việc lấy tên người mượn/người duyệt nhanh chóng
    List<User> findByIdIn(Collection<Long> ids);
    // Tìm kiếm User theo danh sách các Role và phân trang
    Page<User> findByRole_NameIn(List<String> roleNames, Pageable pageable);

    // tìm kiếm theo tên hoặc mã NV kết hợp phân trang sau này
    Page<User> findByRoleInAndFullNameContainingIgnoreCase(List<String> roles, String fullName, Pageable pageable);
}
