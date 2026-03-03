package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_labors", indexes = {
        @Index(name = "idx_labor_task", columnList = "task_id"),
        @Index(name = "idx_labor_user", columnList = "userId"),
        @Index(name = "idx_labor_checkin", columnList = "checkInTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskLabor extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Long userId;

    private LocalDateTime checkInTime;
    private String checkInLocation; // Tọa độ GPS
    private String checkInImage;    // Link ảnh Appwrite

    private LocalDateTime checkOutTime;
    private String checkOutLocation;
    private String checkOutImage;
    private Integer duration;
    private String note;
}
