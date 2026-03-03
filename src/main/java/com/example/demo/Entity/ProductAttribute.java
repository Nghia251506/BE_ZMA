package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_attributes")
@Data
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Ví dụ: Dung lượng, Bus, Màu sắc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Thuộc tính này dành cho loại hàng nào
}
