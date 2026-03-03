package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouses", indexes = {
        @Index(name = "idx_warehouse_code", columnList = "code", unique = true),
        @Index(name = "idx_warehouse_name", columnList = "name")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Ví dụ: KHO01, KHO-HANOI

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private String managerName; // Tên người quản lý kho

    private Boolean active = true;
}
