package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ví dụ: "TASK_CREATE"

    private String description; // Mô tả quyền để hiển thị trên giao diện quản trị
}