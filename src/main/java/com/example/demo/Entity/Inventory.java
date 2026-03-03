package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories", indexes = {
        // Index tổ hợp giúp truy vấn tồn kho của 1 SP tại 1 kho cụ thể cực nhanh
        @Index(name = "idx_inv_product_warehouse", columnList = "product_id, warehouse_id", unique = true),
        // Index lẻ trên warehouse_id để hỗ trợ việc "Xem tất cả sản phẩm trong kho A"
        @Index(name = "idx_inv_warehouse", columnList = "warehouse_id")
})
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity = 0;

    private String binLocation; // Vị trí cụ thể trong kho (Kệ A1, Tầng 2...)
}
