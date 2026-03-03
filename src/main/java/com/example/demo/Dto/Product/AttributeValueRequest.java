package com.example.demo.Dto.Product;

import lombok.Data;

@Data
public class AttributeValueRequest {
    private Long attributeId;
    private String value;
}
