package com.example.demo.Service.User;

import com.example.demo.Dto.RoleAndPermission.RoleResponse;
import com.example.demo.Mapper.RoleMapper;
import com.example.demo.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> getAll() {
        return roleMapper.toDtoList(roleRepository.findAll());
    }
}
