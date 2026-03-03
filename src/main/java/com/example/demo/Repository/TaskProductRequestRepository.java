package com.example.demo.Repository;

import com.example.demo.Entity.RequestStatus;
import com.example.demo.Entity.TaskProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskProductRequestRepository extends JpaRepository<TaskProductRequest, Long> {

    // Xem danh sách vật tư đã mượn của Task (Yêu cầu 3)
    List<TaskProductRequest> findByTaskId(Long taskId);

    // Lọc các yêu cầu mượn chưa trả (Dành cho thủ kho)
    List<TaskProductRequest> findByStatus(RequestStatus status);
    // Lấy tất cả yêu cầu (cho Admin) - Join sẵn Task và Product để Mapper chạy mượt
    @Query("SELECT r FROM TaskProductRequest r " +
            "JOIN FETCH r.task " +
            "WHERE (:status IS NULL OR r.status = :status) " +
            "AND (:keyword IS NULL OR r.task.taskCode LIKE %:keyword% OR r.task.title LIKE %:keyword%)")
    Page<TaskProductRequest> findAllWithDetails(
            @Param("status") RequestStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
