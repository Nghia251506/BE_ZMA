package com.example.demo.Dto.Task.Request;

import lombok.Data;

import java.util.List;

@Data
public class TaskProductReturnRequest {
    private Long requestId; // ID của bản ghi mượn hàng
    private Double usedQuantity; // Số lượng đã lắp thực tế
    private List<String> serials; // Danh sách Serial của thiết bị đã lắp
}
