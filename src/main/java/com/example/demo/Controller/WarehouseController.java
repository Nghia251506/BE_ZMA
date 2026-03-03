package com.example.demo.Controller;

import com.example.demo.Dto.Warehouse.WarehouseRequest;
import com.example.demo.Dto.Warehouse.WarehouseResponse;
import com.example.demo.Service.Warehouse.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<Page<WarehouseResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(warehouseService.getAll(keyword, pageable));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<List<WarehouseResponse>> getActiveWarehouses() {
        return ResponseEntity.ok(warehouseService.getActiveWarehouses());
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        warehouseService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEADER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}