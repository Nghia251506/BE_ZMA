package com.example.demo.Mapper;

import com.example.demo.Dto.RoleAndPermission.RoleResponse;
import com.example.demo.Entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
    RoleResponse toDto(Role role);
    List<RoleResponse> toDtoList(List<Role> roles);
}
