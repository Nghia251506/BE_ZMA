package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "task_product_requests", indexes = {
        @Index(name = "idx_request_task", columnList = "task_id"),
        @Index(name = "idx_request_product", columnList = "productId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskProductRequest extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Long productId;

    private Double borrowQuantity; // Số lượng mượn đi
    private Double usedQuantity;   // Số lượng thực tế đã lắp
    private Double returnQuantity; // Số lượng trả lại kho

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // REQUESTED, EXPENDED, RETURNED

    private String note;

    @OneToMany(mappedBy = "productRequest", cascade = CascadeType.ALL)
    private List<TaskEquipmentSerial> serials;
    private Long warehouseId;
}
