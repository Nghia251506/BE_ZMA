package com.example.demo.Dto.Inventory;

import lombok.Data;

@Data
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;

    private Long warehouseId;
    private String warehouseName;

    private Integer quantity;
    private String binLocation;
}
