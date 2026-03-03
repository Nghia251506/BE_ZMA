package com.example.demo.Mapper;

import com.example.demo.Dto.Task.Response.MaterialRequestResponse;
import com.example.demo.Entity.TaskProductRequest;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MaterialMapper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Mapping(source = "id", target = "requestId")
    @Mapping(source = "task.taskCode", target = "taskCode")
    @Mapping(source = "task.title", target = "taskTitle")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "requesterName", ignore = true)
    @Mapping(target = "requesterMaNV", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "leaderName", ignore = true) // Cần ignore để set thủ công
    @Mapping(target = "leaderMaNV", ignore = true)
    public abstract MaterialRequestResponse toRequestResponse(TaskProductRequest entity);

    @AfterMapping
    protected void mapRelatedEntities(TaskProductRequest entity, @MappingTarget MaterialRequestResponse dto) {
        // 1. Lấy thông tin sản phẩm (Dùng SKU cho productCode)
        if (entity.getProductId() != null) {
            productRepository.findById(entity.getProductId()).ifPresent(p -> {
                dto.setProductName(p.getName());
                dto.setProductCode(p.getSku());
                dto.setUnit(p.getUnit()); // Đừng quên đơn vị tính
            });
        }

        // 2. Lấy thông tin Leader từ Task (Vì Task chỉ lưu leaderId dạng Long)
        if (entity.getTask() != null && entity.getTask().getLeaderId() != null) {
            userRepository.findById(entity.getTask().getLeaderId()).ifPresent(u -> {
                dto.setLeaderId(u.getId());
                dto.setLeaderName(u.getFullName());
                dto.setLeaderMaNV(u.getMaNV());
            });
        }

        // 3. Truy vết người tạo yêu cầu (Requester)
        // Lưu ý: Nếu getCreatedBy() trả về MaNV thì dùng findByMaNV
        if (entity.getCreatedBy() != null) {
            try {
                // Thử tìm theo ID, nếu không được thì tìm theo MaNV/Username tùy DB ông lưu gì
                userRepository.findById(Long.valueOf(entity.getCreatedBy())).ifPresent(u -> {
                    dto.setRequesterName(u.getFullName());
                    dto.setRequesterMaNV(u.getMaNV());
                });
            } catch (Exception e) {
                // Fallback nếu CreatedBy không phải là Long ID
                System.err.println("CreatedBy không phải ID: " + entity.getCreatedBy());
            }
        }
    }
}
