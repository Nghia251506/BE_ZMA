package com.example.demo.Dto.Task.Request;

import lombok.Data;

@Data
public class TaskProductRequestDto {
    private Long productId;
    private Double borrowQuantity;
    private String note;
}
