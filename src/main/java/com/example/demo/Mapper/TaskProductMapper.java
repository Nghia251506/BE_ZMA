package com.example.demo.Mapper;

import com.example.demo.Dto.Task.Request.TaskProductRequestDto;
import com.example.demo.Dto.Task.Response.TaskProductResponse;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.TaskProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskProductMapper {

    // Mượn hàng ban đầu
    @Mapping(target = "status", constant = "REQUESTED")
    @Mapping(target = "returnQuantity", constant = "0.0")
    @Mapping(target = "usedQuantity", constant = "0.0")
    TaskProductRequest toEntity(TaskProductRequestDto dto, Task task);

    // Trả về thông tin để hiển thị danh sách vật tư đã mượn
    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "productName", ignore = true) // Sẽ lấy từ Product Service
    @Mapping(target = "productCode", ignore = true)
    TaskProductResponse toResponse(TaskProductRequest entity);
}
