package com.example.demo.Service.Warehouse;

import com.example.demo.Dto.Warehouse.WarehouseRequest;
import com.example.demo.Dto.Warehouse.WarehouseResponse;
import com.example.demo.Entity.Warehouse;
import com.example.demo.Mapper.WarehouseMapper;
import com.example.demo.Repository.WarehouseRepository;
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
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    // 1. TẠO MỚI (Có gọi Stored Procedure để sinh mã Code)
    public WarehouseResponse create(WarehouseRequest request) {
        // Map từ Request sang Entity
        Warehouse warehouse = warehouseMapper.toEntity(request);

        // Gọi Procedure từ Repository để lấy mã Code: KHO-[ADDRESS]-001
        // Lưu ý: Procedure này ông đã viết trong DB rồi nhé
        String generatedCode = warehouseRepository.generateWarehouseCode(request.getAddress());
        warehouse.setCode(generatedCode);

        // Lưu vào database
        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(warehouse);
    }

    // 2. CẬP NHẬT (Chỉ cập nhật thông tin, không cho sửa Code)
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kho ID: " + id));

        // Sử dụng Mapper để update các field (name, address, phone, manager, active)
        warehouseMapper.updateWarehouseFromRequest(request, warehouse);

        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(warehouse);
    }

    // 3. LẤY CHI TIẾT
    @Transactional(readOnly = true)
    public WarehouseResponse getById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kho ID: " + id));
        return warehouseMapper.toResponse(warehouse);
    }

    // 4. DANH SÁCH PHÂN TRANG & TÌM KIẾM
    @Transactional(readOnly = true)
    public Page<WarehouseResponse> getAll(String keyword, Pageable pageable) {
        // Sử dụng query @Query searchWarehouses trong Repository của ông
        return warehouseRepository.searchWarehouses(keyword, pageable)
                .map(warehouseMapper::toResponse);
    }

    // 5. LẤY TẤT CẢ KHO ĐANG HOẠT ĐỘNG (Dùng cho Dropdown/Select bên FE)
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getActiveWarehouses() {
        return warehouseMapper.toResponseList(warehouseRepository.findAllByActiveTrue());
    }

    // 6. XÓA KHO
    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy kho ID: " + id + " để xóa");
        }
        warehouseRepository.deleteById(id);
    }

    // 7. ĐỔI TRẠNG THÁI NHANH (Tùy chọn thêm cho xịn xò)
    public void toggleActive(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kho"));
        warehouse.setActive(!warehouse.getActive());
        warehouseRepository.save(warehouse);
    }
}
