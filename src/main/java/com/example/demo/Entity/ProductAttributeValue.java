package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_attribute_values")
@Data
public class ProductAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private ProductAttribute attribute;

    @Column(nullable = false)
    private String value; // Ví dụ: 8GB, 3200Mhz, DDR4
}
