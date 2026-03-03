package com.example.demo.Dto.Task.Request;

import lombok.Data;

@Data
public class TaskCheckOutRequest {
    private Long taskId;
    private String location;
    private String imageBase64;
    private String note;
}
