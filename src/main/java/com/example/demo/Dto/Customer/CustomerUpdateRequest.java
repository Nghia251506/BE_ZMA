package com.example.demo.Dto.Customer;

import lombok.Data;

@Data
public class CustomerUpdateRequest extends CustomerCreateRequest {
    private Long id; // Bắt buộc phải có ID để update
}
