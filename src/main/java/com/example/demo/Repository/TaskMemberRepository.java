package com.example.demo.Repository;

import com.example.demo.Entity.TaskMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskMemberRepository extends JpaRepository<TaskMember, Long> {

    // Lấy tất cả anh em hỗ trợ của 1 task (Yêu cầu 2)
    List<TaskMember> findByTaskId(Long taskId);

    // Kiểm tra một User có phải Leader của Task này không (Check quyền chuyển trạng thái)
    Optional<TaskMember> findByTaskIdAndUserIdAndIsLeaderTrue(Long taskId, Long userId);

    void deleteByTaskId(Long id);
}
