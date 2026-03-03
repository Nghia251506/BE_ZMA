package com.example.demo.Mapper;

import com.example.demo.Dto.Auth.UserResponseDTO;
import com.example.demo.Dto.User.UserCreateRequest;
import com.example.demo.Dto.User.UserRequestDTO;
import com.example.demo.Entity.User;
import org.mapstruct.*;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class})
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName() : null)")
    // Mapping permissions từ Authorities (gồm cả Role và Direct Permissions)
    @Mapping(target = "permissions", expression = "java(user.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toSet()))")
    // Thêm các mapping mới nếu tên field trong Entity khác với DTO
    @Mapping(target = "active", expression = "java(user.getStatus() != null && user.getStatus().name().equals(\"ACTIVE\"))")
    @Mapping(target = "id", source = "id")
    UserResponseDTO toDto(User user);

    // Mapping từ Request tạo mới sang Entity
    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", constant = "PENDING") // Mặc định là PENDING khi vừa tạo
    @Mapping(target = "mainPhone", ignore = true)
    @Mapping(target = "subPhone", ignore = true)
    User toEntity(UserCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User user);
}