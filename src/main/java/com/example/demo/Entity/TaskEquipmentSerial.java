package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "task_equipment_serials", indexes = {
        @Index(name = "idx_serial_number", columnList = "serialNumber"),
        @Index(name = "idx_serial_request", columnList = "product_request_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEquipmentSerial extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_request_id")
    private TaskProductRequest productRequest;

    private String serialNumber;
    private Boolean isInstalled; // True là lắp cho khách, False là mang về trả kho
}
