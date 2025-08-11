package com.example.customersms.controller;

import com.example.customersms.dto.request.RoleRequest;
import com.example.customersms.dto.response.RoleResponse;
import com.example.customersms.entity.Roles;
import com.example.customersms.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    // GET /api/v1/admin/roles - Lấy danh sách tất cả các roles
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Roles> roles = roleRepository.findAll();
            List<RoleResponse> roleResponses = roles.stream()
                    .map(RoleResponse::new)
                    .toList();

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Lấy danh sách roles thành công",
                    "data", roleResponses
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Lỗi server: " + e.getMessage()
                    ));
        }
    }

    // GET /api/v1/admin/roles/{id} - Lấy thông tin role theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable("id") Long id) {
        try {
            Optional<Roles> roleData = roleRepository.findById(id);

            if (roleData.isPresent()) {
                RoleResponse roleResponse = new RoleResponse(roleData.get());
                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Lấy thông tin role thành công",
                        "data", roleResponse
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Không tìm thấy role với ID: " + id
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Lỗi server: " + e.getMessage()
                    ));
        }
    }

    // POST /api/v1/admin/roles - Tạo role mới
//    @PostMapping
//    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest roleRequest) {
//        try {
//            // Kiểm tra xem role đã tồn tại chưa
//            if (roleRepository.existsByName(roleRequest.getName())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(Map.of(
//                                "success", false,
//                                "message", "Role " + roleRequest.getName() + " đã tồn tại"
//                        ));
//            }
//
//            Roles newRole = new Roles();
//            newRole.setName(roleRequest.getName());
//            newRole.setDescription(roleRequest.getDescription());
//
//            Roles savedRole = roleRepository.save(newRole);
//            RoleResponse roleResponse = new RoleResponse(savedRole);
//
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(Map.of(
//                            "success", true,
//                            "message", "Tạo role mới thành công",
//                            "data", roleResponse
//                    ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of(
//                            "success", false,
//                            "message", "Lỗi server: " + e.getMessage()
//                    ));
//        }
//    }

    // PUT /api/v1/admin/roles/{id} - Cập nhật thông tin role
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable("id") Long id,
                                        @Valid @RequestBody RoleRequest roleRequest) {
        try {
            Optional<Roles> roleData = roleRepository.findById(id);

            if (roleData.isPresent()) {
                Roles existingRole = roleData.get();

                // Kiểm tra nếu tên role mới đã tồn tại (trừ role hiện tại)
                if (!existingRole.getName().equals(roleRequest.getName()) &&
                        roleRepository.existsByName(roleRequest.getName())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of(
                                    "success", false,
                                    "message", "Role " + roleRequest.getName() + " đã tồn tại"
                            ));
                }

                existingRole.setName(roleRequest.getName());
                existingRole.setDescription(roleRequest.getDescription());

                Roles updatedRole = roleRepository.save(existingRole);
                RoleResponse roleResponse = new RoleResponse(updatedRole);

                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Cập nhật role thành công",
                        "data", roleResponse
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Không tìm thấy role với ID: " + id
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Lỗi server: " + e.getMessage()
                    ));
        }
    }

    // DELETE /api/v1/admin/roles/{id} - Xóa role
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable("id") Long id) {
        try {
            Optional<Roles> roleData = roleRepository.findById(id);

            if (roleData.isPresent()) {
                roleRepository.deleteById(id);
                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Xóa role thành công"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Không tìm thấy role với ID: " + id
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Lỗi server: " + e.getMessage()
                    ));
        }
    }

    // GET /api/v1/admin/roles/search?name={roleName} - Tìm kiếm role theo tên
    @GetMapping("/search")
    public ResponseEntity<?> getRoleByName(@RequestParam("name") String roleName) {
        try {
            // Chuẩn hóa tên role
            String normalizedRoleName = roleName.toUpperCase();
            if (!normalizedRoleName.startsWith("ROLE_")) {
                normalizedRoleName = "ROLE_" + normalizedRoleName;
            }

            Roles.RoleName roleNameEnum = Roles.RoleName.valueOf(normalizedRoleName);
            Optional<Roles> roleData = roleRepository.findByName(roleNameEnum);

            if (roleData.isPresent()) {
                RoleResponse roleResponse = new RoleResponse(roleData.get());
                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Tìm thấy role",
                        "data", roleResponse
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Không tìm thấy role: " + roleName
                        ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Tên role không hợp lệ: " + roleName
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Lỗi server: " + e.getMessage()
                    ));
        }
    }
}