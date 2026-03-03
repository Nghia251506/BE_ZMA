package com.example.demo.Controller;

import com.example.demo.Dto.RoleAndPermission.PermissionResponse;
import com.example.demo.Service.User.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<PermissionResponse>> getAll() {
        return ResponseEntity.ok(permissionService.getAll());
    }
}
