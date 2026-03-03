package com.example.demo.Service.Task;

import com.example.demo.Dto.Inventory.InventoryResponse;
import com.example.demo.Dto.Task.Response.MaterialRequestResponse;
import com.example.demo.Entity.Inventory;
import com.example.demo.Entity.RequestStatus;
import com.example.demo.Entity.TaskProductRequest;
import com.example.demo.Mapper.InventoryMapper;
import com.example.demo.Mapper.MaterialMapper;
import com.example.demo.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialService {

    private final TaskProductRequestRepository requestRepository;
    private final InventoryRepository inventoryRepository;
    private final MaterialMapper materialMapper;
    private final InventoryMapper inventoryMapper;
     private final UserRepository userRepository;
     private final ProductRepository productRepository;
     private final WarehouseRepository warehouseRepository;

    // 1. Lấy danh sách yêu cầu mượn trả (Truy vết tổng hợp)
    public Page<MaterialRequestResponse> getAllRequests(RequestStatus status, String keyword, Pageable pageable) {
        Page<TaskProductRequest> requests = requestRepository.findAllWithDetails(status, keyword, pageable);

        return requests.map(req -> {
            // 1. Map cơ bản từ Entity sang DTO bằng Mapper (lấy taskCode, borrowQuantity,...)
            MaterialRequestResponse dto = materialMapper.toRequestResponse(req);

            // 2. Đi tìm Tên sản phẩm & Mã sản phẩm & Đơn vị
            if (req.getProductId() != null) {
                productRepository.findById(req.getProductId()).ifPresent(p -> {
                    dto.setProductName(p.getName());
                    dto.setProductCode(p.getSku()); // Hoặc p.getCode() tùy DB của ông
                    dto.setUnit(p.getUnit());
                });
            }

            // 3. Đi tìm Leader (Task -> leaderId -> User)
            if (req.getTask() != null && req.getTask().getLeaderId() != null) {
                userRepository.findById(req.getTask().getLeaderId()).ifPresent(u -> {
                    dto.setLeaderId(u.getId());
                    dto.setLeaderName(u.getFullName());
                    dto.setLeaderMaNV(u.getMaNV());
                });
            }

            // 4. Đi tìm Người yêu cầu (CreatedBy là String MaNV hoặc ID)
            if (req.getCreatedBy() != null) {
                try {
                    // Nếu CreatedBy là ID kiểu Long
                    userRepository.findById(Long.valueOf(req.getCreatedBy())).ifPresent(u -> {
                        dto.setRequesterName(u.getFullName());
                        dto.setRequesterMaNV(u.getMaNV());
                    });
                } catch (NumberFormatException e) {
                    // Nếu CreatedBy là Username hoặc MaNV (Dùng findByUsername hoặc findByMaNV)
                    userRepository.findByMaNV(req.getCreatedBy()).ifPresent(u -> {
                        dto.setRequesterName(u.getFullName());
                        dto.setRequesterMaNV(u.getMaNV());
                    });
                }
            }

            // 5. Đi tìm Tên Kho (Nếu đã được duyệt xuất kho)
            if (req.getWarehouseId() != null) {
                // Giả sử ông có warehouseRepository
                 warehouseRepository.findById(req.getWarehouseId()).ifPresent(w -> dto.setWarehouseName(w.getName()));
                // Hoặc tạm thời set cứng nếu chưa có Repository
//                dto.setWarehouseName(req.getWarehouseId() == 1 ? "Kho Tổng Quận 1" : "Kho Chi Nhánh Quận 7");
            }

            return dto;
        });
    }

    // 2. Lấy báo cáo tồn kho (Soi hàng)
    public List<InventoryResponse> getInventory(Long warehouseId, Long productId) {
        return inventoryMapper.toResponseList(
                inventoryRepository.findByWarehouseAndProduct(warehouseId, productId)
        );
    }
}
