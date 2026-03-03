package com.example.demo.Mapper;

import com.example.demo.Dto.Inventory.InventoryResponse;
import com.example.demo.Entity.Inventory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    InventoryResponse toResponse(Inventory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    // Thêm cái này để nếu field nào ở Response bị null thì nó không ghi đè vào Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInventoryFromResponse(InventoryResponse response, @MappingTarget Inventory entity);

    List<InventoryResponse> toResponseList(List<Inventory> entities);
}
