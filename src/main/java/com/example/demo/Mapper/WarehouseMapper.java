package com.example.demo.Mapper;

import com.example.demo.Dto.Warehouse.WarehouseRequest;
import com.example.demo.Dto.Warehouse.WarehouseResponse;
import com.example.demo.Entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequest request);
    WarehouseResponse toResponse(Warehouse entity);
    @Mapping(target = "id", ignore = true) // Không cho phép sửa ID
    @Mapping(target = "code", ignore = true) // Không cho phép sửa mã Kho đã sinh
    void updateWarehouseFromRequest(WarehouseRequest request, @MappingTarget Warehouse entity);
    List<WarehouseResponse> toResponseList(List<Warehouse> entities);
}
