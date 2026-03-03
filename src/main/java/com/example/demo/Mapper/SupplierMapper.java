package com.example.demo.Mapper;

import com.example.demo.Dto.Supplier.SupplierRequest;
import com.example.demo.Dto.Supplier.SupplierResponse;
import com.example.demo.Entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // Để nó trở thành một Spring Bean, tiêm @Autowired được
public interface SupplierMapper {

    // 1. Chuyển từ Request (ZMA gửi lên) sang Entity để lưu DB
    Supplier toEntity(SupplierRequest request);

    // 2. Chuyển từ Entity (DB bốc ra) sang Response để trả về ZMA
    SupplierResponse toResponse(Supplier entity);

    // 3. Dùng cho Update: Cập nhật dữ liệu từ Request vào Entity đã có sẵn
    void updateEntityFromRequest(SupplierRequest request, @MappingTarget Supplier entity);
}