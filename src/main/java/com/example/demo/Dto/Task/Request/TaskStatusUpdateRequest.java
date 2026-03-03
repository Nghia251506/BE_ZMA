package com.example.demo.Dto.Task.Request;

import com.example.demo.Entity.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
    private TaskStatus status; // DONE hoặc CANCELLED
    private String finalNote;
}
