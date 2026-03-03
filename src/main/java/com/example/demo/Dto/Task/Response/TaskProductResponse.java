package com.example.demo.Dto.Task.Response;

import com.example.demo.Entity.RequestStatus;
import lombok.Data;

@Data
public class TaskProductResponse {
    private Long requestId;
    private String productName;
    private String productCode;
    private Double borrowQuantity;
    private Double usedQuantity;
    private Double returnQuantity;
    private RequestStatus status;
}
