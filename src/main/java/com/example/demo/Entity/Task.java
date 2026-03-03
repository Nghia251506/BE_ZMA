package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_task_code", columnList = "taskCode"),
        @Index(name = "idx_task_leader", columnList = "leaderId"),
        @Index(name = "idx_task_customer", columnList = "customerId"),
        @Index(name = "idx_task_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String taskCode;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // NEW, ASSIGNED, IN_PROGRESS, WAITING_CONFIRM, DONE, CANCELLED

    @Enumerated(EnumType.STRING)
    private Priority priority; // LOW, MEDIUM, HIGH, URGENT

    private Long leaderId; // User ID của người chịu trách nhiệm chính
    private Long customerId;

    private LocalDateTime appointmentDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private Integer totalDuration; // Tính bằng phút

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskMember> members= new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskProductRequest> productRequests;
}
