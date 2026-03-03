package com.example.demo.Dto.Task.Request;

import com.example.demo.Entity.Priority;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private Priority priority;
    private Long leaderId;
    private Long customerId;
    private LocalDateTime appointmentDate;
    private List<Long> supporterIds; // Danh sách ID anh em đi hỗ trợ
}
