package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: RAM, CPU

    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
