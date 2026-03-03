package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_members", indexes = {
        @Index(name = "idx_member_task", columnList = "task_id"),
        @Index(name = "idx_member_user", columnList = "userId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMember extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Long userId;
    private Boolean isLeader; // Quyền chuyển trạng thái task
}
