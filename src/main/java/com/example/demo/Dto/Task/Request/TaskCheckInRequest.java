package com.example.demo.Dto.Task.Request;

import lombok.Data;

@Data
public class TaskCheckInRequest {
    private Long taskId;
    private String location;
    private String imageBase64; // Hoặc link ảnh nếu upload trước
}
