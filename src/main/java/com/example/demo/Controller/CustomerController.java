package com.example.demo.Controller;

import com.example.demo.Dto.Customer.CustomerCreateRequest;
import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Dto.Customer.CustomerUpdateRequest;
import com.example.demo.Service.Customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    // 1. Xem danh sách khách hàng
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW')")
    public ResponseEntity<Page<CustomerResponseDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CustomerResponseDto> result = customerService.getAllCustomers(search, pageable);
        return ResponseEntity.ok(result);
    }

    // 2. Thêm mới khách hàng
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
    public ResponseEntity<CustomerResponseDto> create(@RequestBody CustomerCreateRequest request) {
        CustomerResponseDto response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 3. Cập nhật thông tin khách hàng
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_EDIT')")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequest request
    ) {
        CustomerResponseDto response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    // 4. Xóa khách hàng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}