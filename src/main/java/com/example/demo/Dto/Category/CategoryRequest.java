package com.example.demo.Dto.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String description;
}
