package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_sku", columnList = "sku", unique = true),
        @Index(name = "idx_product_barcode", columnList = "barcode"),
        @Index(name = "idx_product_name", columnList = "name")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku; // SP000001

    @Column(nullable = false)
    private String name;

    private String barcode;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>(); // Cho Appwrite

    private Double costPrice;
    private Double sellingPrice;
    private Integer minStock; // Ngưỡng cảnh báo chung
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Liên kết để dễ dàng lấy tổng tồn kho từ tất cả các kho
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttributeValue> attributeValues = new ArrayList<>();
}
