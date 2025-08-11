package com.example.customersms.controller;

import com.example.customersms.dto.request.CustomerGroupRequest;
import com.example.customersms.dto.response.CustomerGroupResponse;
import com.example.customersms.service.CustomerGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer-groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerGroupController {

    private final CustomerGroupService customerGroupService;

    /**
     * GET /api/customer-groups - Lấy danh sách nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @GetMapping
    public ResponseEntity<Page<CustomerGroupResponse>> getAllCustomerGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerGroupResponse> customerGroups = customerGroupService.getAllCustomerGroups(pageable);

        return ResponseEntity.ok(customerGroups);
    }

    /**
     * GET /api/customer-groups/list - Lấy danh sách đơn giản (không phân trang)
     * Quyền: ADMIN, STAFF
     */
    @GetMapping("/list")
    public ResponseEntity<List<CustomerGroupResponse>> getCustomerGroupsList() {
        List<CustomerGroupResponse> customerGroups = customerGroupService.getAllCustomerGroupsList();
        return ResponseEntity.ok(customerGroups);
    }

    /**
     * GET /api/customer-groups/{id} - Lấy thông tin chi tiết nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerGroupResponse> getCustomerGroupById(@PathVariable Long id) {
        CustomerGroupResponse customerGroup = customerGroupService.getCustomerGroupById(id);
        return ResponseEntity.ok(customerGroup);
    }

    /**
     * POST /api/customer-groups - Tạo nhóm khách hàng mới
     * Quyền: ADMIN, STAFF
     */
    @PostMapping
    public ResponseEntity<CustomerGroupResponse> createCustomerGroup(
            @Valid @RequestBody CustomerGroupRequest request) {
        CustomerGroupResponse createdGroup = customerGroupService.createCustomerGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    /**
     * PUT /api/customer-groups/{id} - Cập nhật thông tin nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerGroupResponse> updateCustomerGroup(
            @PathVariable Long id,
            @Valid @RequestBody CustomerGroupRequest request) {
        CustomerGroupResponse updatedGroup = customerGroupService.updateCustomerGroup(id, request);
        return ResponseEntity.ok(updatedGroup);
    }

    /**
     * DELETE /api/customer-groups/{id} - Xóa nhóm khách hàng
     * Quyền: ADMIN
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerGroup(@PathVariable Long id) {
        customerGroupService.deleteCustomerGroup(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/customer-groups/search - Tìm kiếm nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerGroupResponse>> searchCustomerGroups(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerGroupResponse> results = customerGroupService.searchCustomerGroups(keyword, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/customer-groups/count - Đếm tổng số nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCustomerGroups() {
        Long count = customerGroupService.countCustomerGroups();
        return ResponseEntity.ok(count);
    }

    /**
     * PATCH /api/customer-groups/{id}/status - Thay đổi trạng thái nhóm khách hàng
     * Quyền: ADMIN, STAFF
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerGroupResponse> changeCustomerGroupStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        CustomerGroupResponse updatedGroup = customerGroupService.changeCustomerGroupStatus(id, active);
        return ResponseEntity.ok(updatedGroup);
    }
}