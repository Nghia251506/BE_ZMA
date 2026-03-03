package com.example.demo.Service.Supplier;

import com.example.demo.Dto.Supplier.SupplierRequest;
import com.example.demo.Dto.Supplier.SupplierResponse;
import com.example.demo.util.ResourceNotFoundException;
import com.example.demo.Mapper.SupplierMapper;
import com.example.demo.Entity.Supplier;
import com.example.demo.Repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    /**
     * 1. Lấy danh sách NCC có phân trang
     * Chỉ lấy những ông chưa bị xóa (deletedAt == null)
     */
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(String keyword, Pageable pageable) {
        Page<Supplier> supplierPage;

        // Kiểm tra nếu keyword có giá trị thì mới search
        if (keyword != null && !keyword.trim().isEmpty()) {
            supplierPage = supplierRepository.searchSuppliers(keyword.trim(), pageable);
        } else {
            // Nếu không có keyword, lấy toàn bộ bình thường
            supplierPage = supplierRepository.findAll(pageable);
        }

        // Map kết quả sang DTO
        return supplierPage.map(supplierMapper::toResponse);
    }

    /**
     * 2. Lấy chi tiết 1 ông NCC theo ID
     */
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Nhà cung cấp với ID: " + id));

        // Nếu ông dùng Soft Delete, check thêm:
        if (supplier.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Nhà cung cấp này đã bị ngừng hợp tác (đã xóa)");
        }

        return supplierMapper.toResponse(supplier);
    }

    /**
     * 3. Tạo mới Nhà cung cấp
     */
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        // 1. Tự động lấy mã từ Procedure
        String autoCode = supplierRepository.getNextSupplierCode();

        // 2. Map từ Request sang Entity
        Supplier supplier = supplierMapper.toEntity(request);

        // 3. Gán mã vừa sinh vào Entity
        supplier.setCode(autoCode);

        // 4. Lưu bản ghi
        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponse(savedSupplier);
    }

    /**
     * 4. Cập nhật thông tin Nhà cung cấp
     */
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Nhà cung cấp cần cập nhật"));

        // Dùng MapStruct để map đè dữ liệu từ Request vào Entity hiện tại
        supplierMapper.updateEntityFromRequest(request, existingSupplier);

        // BaseEntity tự lo updatedAt và updatedBy
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toResponse(updatedSupplier);
    }

    /**
     * 5. Xóa Nhà cung cấp (Soft Delete)
     * Thay vì xóa khỏi DB, mình chỉ đánh dấu ngày xóa để giữ lịch sử hóa đơn/nhập hàng
     */
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Nhà cung cấp để xóa"));

        // Đánh dấu xóa mềm
        supplier.setDeletedAt(LocalDateTime.now());
        // deletedBy sẽ được JPA Auditing lo nếu ông config đúng

        supplierRepository.save(supplier);
    }
}
