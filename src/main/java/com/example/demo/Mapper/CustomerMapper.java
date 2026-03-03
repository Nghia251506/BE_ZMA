package com.example.demo.Mapper;

import com.example.demo.Dto.Customer.CustomerCreateRequest;
import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Dto.Customer.CustomerUpdateRequest;
import com.example.demo.Entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    /**
     * Map từ Request sang Entity (Dùng cho Create/Update)
     * Các trường trùng tên sẽ tự map, các trường ID sẽ được xử lý riêng ở Service
     * hoặc dùng @Mapping ở đây nếu ông muốn.
     */
    Customer toEntity(CustomerCreateRequest request);

    @Mapping(target = "id", ignore = true) // Đảm bảo không ghi đè ID khi update
    void updateEntityFromRequest(CustomerUpdateRequest request, @MappingTarget Customer customer);

    /**
     * Map từ Entity sang Response (DTO trả về FE)
     */
    @Mappings({
            // Logic COALESCE: Nếu Customer.fullName null -> lấy User.fullName
            @Mapping(target = "fullName", expression = "java(entity.getFullName() != null ? entity.getFullName() : (entity.getUser() != null ? entity.getUser().getFullName() : null))"),
            @Mapping(target = "email", expression = "java(entity.getEmail() != null ? entity.getEmail() : (entity.getUser() != null ? entity.getUser().getEmail() : null))"),
//            @Mapping(target = "phone", expression = "java(entity.getPhone() != null ? entity.getPhone() : (entity.getUser() != null ? entity.getUser().getMainPhone() : null))"),
            @Mapping(target = "mainPhone", expression = "java(entity.getMainPhone() != null ? entity.getMainPhone() : (entity.getUser() != null ? entity.getUser().getMainPhone() : null))"),
            @Mapping(target = "subPhone",expression = "java(entity.getSubPhone() != null ? entity.getSubPhone() : (entity.getUser() != null ? entity.getUser().getSubPhone() : null))"),
            // Lấy maNV của nhóm khách hàng
            @Mapping(target = "customerGroup", source = "customerGroupUser.maNV", defaultValue = "KH Mới"),

            // Lấy tên người phụ trách và người tạo
            @Mapping(target = "assigneeName", source = "assignee.maNV"),
            @Mapping(target = "creatorName", source = "creator.maNV")
    })
    CustomerResponseDto toResponseDto(Customer entity);
}
