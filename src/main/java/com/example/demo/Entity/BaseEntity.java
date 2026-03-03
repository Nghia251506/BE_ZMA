package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass // Đánh dấu đây là lớp cha, không tạo bảng riêng
@EntityListeners(AuditingEntityListener.class) // Lắng nghe để tự động điền ngày giờ
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // Dùng cho Soft Delete (Xóa mềm)

    @CreatedBy
    @Column(updatable = false)
    private String createdBy; // Lưu username người tạo

    @LastModifiedBy
    private String updatedBy; // Lưu username người cập nhật cuối

    private String deletedBy;
}
