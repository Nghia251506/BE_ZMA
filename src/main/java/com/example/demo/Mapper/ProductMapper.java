package com.example.demo.Mapper;

import com.example.demo.Dto.Product.ProductAttributeValueResponse;
import com.example.demo.Dto.Product.ProductRequest;
import com.example.demo.Dto.Product.ProductResponse;
import com.example.demo.Dto.Inventory.InventoryDetailResponse; // Nhớ import cái này
import com.example.demo.Entity.Product;
import com.example.demo.Entity.ProductAttributeValue;
import com.example.demo.Entity.Inventory; // Import entity Inventory
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    // --- FROM REQUEST TO ENTITY ---
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "supplier.id", source = "supplierId")
    @Mapping(target = "attributeValues", ignore = true) // Xử lý logic phức tạp ở Service
    @Mapping(target = "inventories", ignore = true)    // Xử lý khởi tạo tồn kho ở Service
    Product toEntity(ProductRequest request);

    // --- FROM ENTITY TO RESPONSE ---
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "attributes", source = "attributeValues")
    @Mapping(target = "inventoryDetails", source = "inventories") // Map List<Inventory> -> List<InventoryDetailResponse>
    @Mapping(target = "totalQuantity", expression = "java(calculateTotalQuantity(entity))")
    ProductResponse toResponse(Product entity);

    // Helper: Map từng dòng thuộc tính
    @Mapping(target = "attributeName", source = "attribute.name")
    ProductAttributeValueResponse toAttrResponse(ProductAttributeValue entity);

    // Helper: Map chi tiết từng kho (FE dùng để hiện bảng tồn kho chi tiết)
    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "binLocation", source = "binLocation")
    InventoryDetailResponse toInventoryDetailResponse(Inventory entity);

    // --- UPDATE LOGIC ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "supplier.id", source = "supplierId")
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product entity);

    // Hàm tính tổng tồn kho
    default Integer calculateTotalQuantity(Product entity) {
        if (entity.getInventories() == null) return 0;
        return entity.getInventories().stream()
                .mapToInt(inv -> inv.getQuantity() != null ? inv.getQuantity() : 0)
                .sum();
    }
}