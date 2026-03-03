package com.example.demo.Service.User;

import com.example.demo.Dto.RoleAndPermission.PermissionResponse;
import com.example.demo.Mapper.PermissionMapper;
import com.example.demo.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public List<PermissionResponse> getAll() {
        return permissionMapper.toDtoList(permissionRepository.findAll());
    }
}
