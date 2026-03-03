package com.example.demo.Dto.RoleAndPermission;

import java.util.Set;
import lombok.*;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private Set<PermissionResponse> permissions; // Để xem Role này có những quyền gì
}
