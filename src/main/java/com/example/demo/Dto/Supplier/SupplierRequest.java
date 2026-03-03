package com.example.demo.Dto.Supplier;

import com.example.demo.Entity.SupplierType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;

    private String taxCode;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String email;
    private String address;
    private String contactName;
    private SupplierType type; // GOODS, SERVICE, BOTH
    private String note;
}
