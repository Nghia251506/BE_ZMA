package com.example.demo.Mapper;

import com.example.demo.Dto.Task.Request.TaskCheckInRequest;
import com.example.demo.Dto.Task.Request.TaskCheckOutRequest;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.TaskLabor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskLaborMapper {

    // Tạo bản ghi Check-in
    @Mapping(target = "checkInTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "checkInLocation", source = "request.location")
    @Mapping(target = "checkInImage", source = "request.imageBase64")
    TaskLabor toCheckInEntity(TaskCheckInRequest request, Long userId, Task task);

    // Cập nhật bản ghi Check-out
    @Mapping(target = "checkOutTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "checkOutLocation", source = "request.location")
    @Mapping(target = "checkOutImage", source = "request.imageBase64")
    void updateCheckOut(@MappingTarget TaskLabor labor, TaskCheckOutRequest request);
}
