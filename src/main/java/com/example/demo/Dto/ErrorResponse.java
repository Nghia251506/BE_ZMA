package com.example.demo.Dto;

import lombok.*;

@Data
@AllArgsConstructor // Cái này cực kỳ quan trọng để fix lỗi "found 3"
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}
