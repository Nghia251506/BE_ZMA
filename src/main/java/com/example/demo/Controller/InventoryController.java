package com.example.demo.Controller;

import com.example.demo.Service.Warehouse.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * API Chuyển kho
     * URL ví dụ: /api/v1/inventory/transfer?productId=1&fromId=1&toId=2&qty=5
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<String> transferProduct(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam Integer quantity) {

        inventoryService.transferProduct(productId, fromWarehouseId, toWarehouseId, quantity);
        return ResponseEntity.ok("Điều chuyển hàng thành công!");
    }
}