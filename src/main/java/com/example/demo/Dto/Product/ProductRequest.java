package com.example.demo.Dto.Product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String barcode;
    private Double costPrice;
    private Double sellingPrice;
    private String unit;
    private Integer minStock;

    // Liên kết
    private Long supplierId;
    private Long categoryId;


    // Hình ảnh từ Appwrite
    private List<String> imageUrls;

    // QUAN TRỌNG: Nhập kho ngay khi tạo
    private Long warehouseId;      // Chọn kho lúc thêm mới
    private Integer initialQuantity; // Số lượng nhập đầu kỳ (ví dụ nhập 100 cái)
    private String binLocation;    // Vị trí kệ (nếu có)

    // Danh sách thuộc tính động
    private List<AttributeValueRequest> attributes;
}
