package com.example.demo.Dto.Warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRequest {
    @NotBlank(message = "Tên kho không được để trống")
    private String name;

    private String address;
    private String phone;
    private String managerName;
    private Boolean active = true;
}
