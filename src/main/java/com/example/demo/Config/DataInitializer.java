//package com.example.demo.Config;
//
//import com.example.demo.Entity.Permission;
//import com.example.demo.Entity.Role;
//import com.example.demo.Repository.PermissionRepository;
//import com.example.demo.Repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//    private final PermissionRepository permissionRepository;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        log.info("--- Bắt đầu khởi tạo dữ liệu mẫu (Roles & Permissions) ---");
//
//        // 1. Khởi tạo danh sách Permission mẫu
//        Permission readTask = createPermissionIfNotFound("TASK_READ", "Quyền xem công việc");
//        Permission createTask = createPermissionIfNotFound("TASK_CREATE", "Quyền tạo mới công việc");
//        Permission deleteUser = createPermissionIfNotFound("USER_DELETE", "Quyền xóa nhân viên");
//
//        // 2. Khởi tạo Role ADMIN (Có tất cả quyền)
//        createRoleIfNotFound("ADMIN", "Quản trị viên hệ thống", Set.of(readTask, createTask, deleteUser));
//
//        // 3. Khởi tạo Role STAFF (Chỉ có quyền xem)
//        createRoleIfNotFound("STAFF", "Nhân viên chính thức", Set.of(readTask));
//
//        log.info("--- Khởi tạo dữ liệu hoàn tất! ---");
//    }
//
//    private Permission createPermissionIfNotFound(String name, String description) {
//        return permissionRepository.findByName(name).orElseGet(() -> {
//            Permission p = new Permission();
//            p.setName(name);
//            p.setDescription(description);
//            return permissionRepository.save(p);
//        });
//    }
//
//    private void createRoleIfNotFound(String name, String description, Set<Permission> permissions) {
//        if (roleRepository.findByName(name).isEmpty()) {
//            Role role = new Role();
//            role.setName(name);
////            role.setDescription(description);
//            role.setPermissions(permissions);
//            roleRepository.save(role);
//            log.info("Đã tạo Role: {}", name);
//        }
//    }
//}