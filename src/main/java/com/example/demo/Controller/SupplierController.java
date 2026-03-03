package com.example.demo.Controller;

import com.example.demo.Dto.Supplier.SupplierRequest;
import com.example.demo.Dto.Supplier.SupplierResponse;
import com.example.demo.Service.Supplier.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Lấy danh sách NCC - Nhân viên (STAFF) trở lên là xem được để gọi điện/giao máy
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    public ResponseEntity<Page<SupplierResponse>> getAll(
            @RequestParam(required = false) String keyword, // Thêm param này
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // Truyền keyword vào Service
        return ResponseEntity.ok(supplierService.getAllSuppliers(keyword, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPPLIER_CREATE')") // Chỉ ai có quyền CREATE mới được thêm
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return new ResponseEntity<>(supplierService.createSupplier(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_UPDATE')")
    public ResponseEntity<SupplierResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}