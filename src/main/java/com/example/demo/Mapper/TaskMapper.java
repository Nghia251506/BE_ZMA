package com.example.demo.Mapper;

import com.example.demo.Dto.Task.Request.TaskCreateRequest;
import com.example.demo.Dto.Task.Response.TaskDetailResponse;
import com.example.demo.Dto.Task.Response.TaskLaborResponse;
import com.example.demo.Dto.Task.Response.TaskMemberResponse;
import com.example.demo.Dto.Task.Response.TaskProductResponse;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.TaskLabor;
import com.example.demo.Entity.TaskMember;
import com.example.demo.Entity.TaskProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // 1. Tạo nhiệm vụ (Yêu cầu 1)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "taskCode", expression = "java(generateTaskCode())")
    @Mapping(target = "members", ignore = true) // <--- CHẶN ĐỨT THỦ PHẠM TẠI ĐÂY
    @Mapping(target = "productRequests", ignore = true)
    Task toEntity(TaskCreateRequest request);

    // 2. Trả về chi tiết Task - Cần Map các List con
    @Mapping(target = "leader", ignore = true) // Sẽ set thủ công trong Service để lấy tên từ DB
    @Mapping(target = "members", source = "members") // Tự động map sang List<TaskMemberResponse>
    @Mapping(target = "productRequests", source = "productRequests") // Tự động map sang List<TaskProductResponse>
    @Mapping(target = "labors", ignore = true) // Tự động map sang List<TaskLaborResponse>
    @Mapping(target = "appointmentDate", source = "appointmentDate")
    TaskDetailResponse toDetailResponse(Task task);



    // 3. Mapper cho Sản phẩm mượn (Giải quyết ProductName bị null)
    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "productName", ignore = true) // Tên sản phẩm phải lấy từ bảng Product (Service sẽ lo)
    TaskProductResponse toProductResponse(TaskProductRequest entity);

    // 4. Mapper cho Thành viên thi công (Yêu cầu 2)
    @Mapping(target = "fullName", ignore = true) // Tên nhân viên lấy từ bảng User (Service sẽ lo)
    TaskMemberResponse toMemberResponse(TaskMember entity);

    // 5. Mapper cho Lịch sử Check-in/out (Yêu cầu 4)
    @Mapping(target = "fullName", ignore = true) // Tên nhân viên lấy từ bảng User (Service sẽ lo)
    TaskLaborResponse toLaborResponse(TaskLabor entity);

    // Update thông tin cơ bản
    @Mapping(target = "members", ignore = true) // Bỏ qua khi update để tránh null list
    void updateEntityFromRequest(TaskCreateRequest request, @MappingTarget Task task);

    // Sinh mã Task
    default String generateTaskCode() {
        return "CV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + (int)(Math.random() * 1000);
    }
}
