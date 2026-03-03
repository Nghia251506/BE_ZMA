package com.example.demo.Mapper;

import com.example.demo.Dto.RoleAndPermission.PermissionResponse;
import com.example.demo.Entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "id", source = "id")
    PermissionResponse toDto(Permission permission);
    List<PermissionResponse> toDtoList(List<Permission> permissions);
}
