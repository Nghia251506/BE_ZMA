package com.example.demo.Dto.Supplier;

import com.example.demo.Entity.SupplierType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierResponse {
    private Long id;
    private String code;
    private String name;
    private String taxCode;
    private String phone;
    private String email;
    private String address;
    private String contactName;
    private SupplierType type;
    private String note;

    // Các trường bốc từ BaseEntity
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
}
