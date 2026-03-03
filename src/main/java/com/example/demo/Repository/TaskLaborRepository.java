package com.example.demo.Repository;

import com.example.demo.Entity.TaskLabor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskLaborRepository extends JpaRepository<TaskLabor, Long> {

    // Lấy lịch sử thi công của 1 task (Sắp xếp theo thời gian mới nhất)
    List<TaskLabor> findByTaskIdOrderByCheckInTimeDesc(Long taskId);

    // Tìm bản ghi Check-in hiện tại của 1 User (Để làm Checkout)
    Optional<TaskLabor> findByTaskIdAndUserIdAndCheckOutTimeIsNull(Long taskId, Long userId);
}
