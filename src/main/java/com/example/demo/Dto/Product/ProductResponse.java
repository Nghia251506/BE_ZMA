package com.example.demo.Dto.Product;

import com.example.demo.Dto.Inventory.InventoryDetailResponse;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String barcode;
    private Double costPrice;
    private Double sellingPrice;
    private String unit;
    private Integer minStock;
    private List<String> imageUrls;

    // Thông tin Phân loại
    private Long categoryId;
    private String categoryName;

    // Thông tin Nhà cung cấp
    private Long supplierId;
    private String supplierName;

    // Thông tin Thuộc tính (RAM: 8GB, Bus: 3200...)
    private List<ProductAttributeValueResponse> attributes;

    // Thông tin Tồn kho chi tiết (Để FE hiển thị: Kho HN: 50, Kho HCM: 30...)
    private List<InventoryDetailResponse> inventoryDetails;

    // Tổng tồn kho tất cả các nơi
    private Integer totalQuantity;
}
