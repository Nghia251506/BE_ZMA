package com.example.demo.Dto.Inventory;

import lombok.Data;

@Data
public class InventoryDetailResponse {
    private String warehouseName;
    private Integer quantity;
    private String binLocation;
}
