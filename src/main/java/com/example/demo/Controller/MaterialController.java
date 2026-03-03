package com.example.demo.Controller;

import com.example.demo.Dto.Inventory.InventoryResponse;
import com.example.demo.Dto.Task.Response.MaterialRequestResponse;
import com.example.demo.Entity.RequestStatus;
import com.example.demo.Service.Task.MaterialService;
import com.example.demo.Service.Task.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Material Management", description = "API dành cho Admin quản lý kho và điều phối vật tư")
public class MaterialController {

    private final MaterialService materialService;
    private final TaskService taskService; // Dùng TaskService để gọi hàm duyệt lẻ

    // ==========================================
    // 1. TRUY VẾT MƯỢN TRẢ (Material Tracking)
    // ==========================================

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<MaterialRequestResponse>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(materialService.getAllRequests(status, keyword, pageable));
    }

    // ==========================================
    // 2. QUẢN LÝ TỒN KHO (Inventory Report)
    // ==========================================

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryResponse>> getInventory(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long productId) {
        return ResponseEntity.ok(materialService.getInventory(warehouseId, productId));
    }
}
