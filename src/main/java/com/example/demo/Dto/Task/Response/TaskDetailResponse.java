package com.example.demo.Dto.Task.Response;

import com.example.demo.Dto.Auth.UserResponseDTO;
import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Entity.Priority;
import com.example.demo.Entity.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDetailResponse {
    private Long id;
    private String taskCode;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;

    private CustomerResponseDto customer;
    private LocalDateTime appointmentDate;

    // Thông tin nhân sự
    private UserResponseDTO leader;
    private List<UserResponseDTO> members;

    // Thông tin thi công
    private List<TaskLaborResponse> labors;

    // Thông tin vật tư/thiết bị
    private List<TaskProductResponse> productRequests;

    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
}
