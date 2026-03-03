package com.example.demo.Controller;

import com.example.demo.Dto.Task.Request.*;
import com.example.demo.Dto.Task.Response.TaskDetailResponse;
import com.example.demo.Entity.TaskStatus;
import com.example.demo.Entity.User;
import com.example.demo.Service.Task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    // ==========================================
    // 1. TẠO & XEM DANH SÁCH (ADMIN/LEADER/STAFF)
    // ==========================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<TaskDetailResponse> create(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<Page<TaskDetailResponse>> getAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(taskService.getAll(pageable, status, keyword));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<TaskDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    // ==========================================
    // 2. NHÂN SỰ (CHỈ ADMIN/LEADER)
    // ==========================================

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<String> assignSupporters(@PathVariable Long id, @RequestBody List<Long> supporterIds) {
        taskService.assignSupporters(id, supporterIds);
        return ResponseEntity.ok("Đã gán nhân sự hỗ trợ thành công.");
    }

    // ==========================================
    // 3. MƯỢN / TRẢ & DUYỆT KHO (QUAN TRỌNG)
    // ==========================================

    // Nhân viên gửi yêu cầu mượn
    @PostMapping("/{id}/borrow")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<String> requestBorrow(@PathVariable Long id, @RequestBody List<TaskProductRequestDto> products) {
        taskService.requestProducts(id, products);
        return ResponseEntity.ok("Đã gửi yêu cầu mượn thiết bị.");
    }

    // Admin duyệt xuất kho (Trừ tồn kho thật)
    @PostMapping("/requests/{requestId}/approve-borrow")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> approveBorrow(
            @PathVariable Long requestId,
            @RequestParam Long warehouseId) {
        taskService.approveBorrow(requestId, warehouseId);
        return ResponseEntity.ok("Đã duyệt xuất kho. Tồn kho đã bị trừ.");
    }

    // Nhân viên báo cáo trả hàng
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<String> reportReturn(@PathVariable Long id, @RequestBody List<TaskProductReturnRequest> returns) {
        taskService.returnProducts(id, returns);
        return ResponseEntity.ok("Đã báo cáo trả hàng. Đang chờ thủ kho xác nhận.");
    }

    // Admin duyệt nhập kho hàng thừa (Cộng tồn kho thật)
    @PostMapping("/requests/{requestId}/approve-return") // Dùng requestId cho chuẩn
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> approveReturn(@PathVariable Long requestId) {
        taskService.approveReturn(requestId); // Service tự tìm warehouseId đã lưu
        return ResponseEntity.ok("Đã xác nhận nhận hàng trả lại. Tồn kho đã cộng lại.");
    }

    // ==========================================
    // 4. CHECK-IN / CHECK-OUT
    // ==========================================

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<String> checkIn(@Valid @RequestBody TaskCheckInRequest request, @AuthenticationPrincipal User currentUser) {
        taskService.checkIn(request, currentUser.getId());
        return ResponseEntity.ok("Check-in thành công.");
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<String> checkOut(@Valid @RequestBody TaskCheckOutRequest request, @AuthenticationPrincipal User currentUser) {
        taskService.checkOut(request, currentUser.getId());
        return ResponseEntity.ok("Check-out thành công.");
    }

    // ==========================================
    // 5. TRẠNG THÁI & XÓA
    // ==========================================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            Authentication authentication,
            @AuthenticationPrincipal User currentUser) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        taskService.updateTaskStatus(id, status, currentUser.getId(), isAdmin);
        return ResponseEntity.ok("Trạng thái nhiệm vụ đã cập nhật: " + status);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<TaskDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER', 'ROLE_STAFF')")
    public ResponseEntity<Page<TaskDetailResponse>> getMyTasks(
            @AuthenticationPrincipal User currentUser, // Spring tự map vào đây
            @PageableDefault(size = 10) Pageable pageable) {

        // Lấy zaloId trực tiếp từ Entity User
        String zaloId = currentUser.getZaloId();
        return ResponseEntity.ok(taskService.getMyTasksForStaff(zaloId, pageable));
    }
}