package com.example.demo.Dto.RoleAndPermission;
import lombok.*;
@Getter
@Setter
@ToString
@Data
@Builder
public class PermissionResponse {
    private Long id;
    private String name;
    private String description;
}
