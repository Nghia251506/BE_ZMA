package com.example.demo.Dto.Warehouse;

import lombok.Data;

@Data
public class WarehouseResponse {
    private Long id;
    private String code; // KHO000001 (Sinh bằng Procedure)
    private String name;
    private String address;
    private String phone;
    private String managerName;
    private Boolean active;
}
