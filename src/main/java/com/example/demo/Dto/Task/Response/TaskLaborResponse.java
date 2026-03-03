package com.example.demo.Dto.Task.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskLaborResponse {
    private String fullName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String durationFormatted; // VD: "2h 30p"
}
