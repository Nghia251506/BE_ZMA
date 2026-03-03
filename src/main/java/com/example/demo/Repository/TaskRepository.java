package com.example.demo.Repository;

import com.example.demo.Entity.Task;
import com.example.demo.Entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Admin xem danh sách task phân trang (Yêu cầu Admin)
    Page<Task> findAll(Pageable pageable);

    // Lọc task theo trạng thái (VD: Xem các task đang IN_PROGRESS)
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    // Tìm kiếm theo mã task hoặc tiêu đề (Search bar cho Admin)
    Page<Task> findByTaskCodeContainingOrTitleContaining(String code, String title, Pageable pageable);

    // Lấy danh sách task của một Leader cụ thể (Yêu cầu 5: Permission)
    List<Task> findByLeaderIdAndStatusNot(Long leaderId, TaskStatus status);

    // Thống kê task theo khoảng thời gian (Báo cáo cho sếp)
    Page<Task> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.members m " +
            "JOIN User u ON m.userId = u.id " +
            "WHERE u.zaloId = :zaloId " +
            "AND t.status NOT IN (com.example.demo.Entity.TaskStatus.DONE, com.example.demo.Entity.TaskStatus.CANCELLED)")
    Page<Task> findTasksByMemberZaloId(@Param("zaloId") String zaloId, Pageable pageable);
}
