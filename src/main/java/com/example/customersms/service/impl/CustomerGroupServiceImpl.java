package com.example.customersms.service.impl;

import com.example.customersms.dto.request.CustomerGroupRequest;
import com.example.customersms.dto.response.CustomerGroupResponse;
import com.example.customersms.entity.CustomerGroup;
import com.example.customersms.config.exception.ResourceNotFoundException;
import com.example.customersms.config.exception.DuplicateResourceException;
import com.example.customersms.repository.CustomerGroupRepository;
import com.example.customersms.service.CustomerGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerGroupServiceImpl implements CustomerGroupService {

    private final CustomerGroupRepository customerGroupRepository;

    @Override
    public Page<CustomerGroupResponse> getAllCustomerGroups(Pageable pageable) {
        log.debug("Getting all customer groups with pagination: {}", pageable);
        Page<CustomerGroup> customerGroups = customerGroupRepository.findAll(pageable);
        return customerGroups.map(this::mapToResponse);
    }

    @Override
    public List<CustomerGroupResponse> getAllCustomerGroupsList() {
        log.debug("Getting all customer groups list");
        List<CustomerGroup> customerGroups = customerGroupRepository.findAll();
        return customerGroups.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerGroupResponse getCustomerGroupById(Long id) {
        log.debug("Getting customer group by id: {}", id);
        CustomerGroup customerGroup = customerGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm khách hàng với ID: " + id));
        return mapToResponse(customerGroup);
    }

    @Override
    public CustomerGroupResponse createCustomerGroup(CustomerGroupRequest request) {
        log.debug("Creating new customer group: {}", request.getGroupName());

        if (customerGroupRepository.existsByGroupNameIgnoreCase(request.getGroupName())) {
            throw new DuplicateResourceException("Tên nhóm khách hàng đã tồn tại: " + request.getGroupName());
        }

        CustomerGroup customerGroup = CustomerGroup.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();

        CustomerGroup savedGroup = customerGroupRepository.save(customerGroup);
        log.info("Created customer group with ID: {}", savedGroup.getId());

        return mapToResponse(savedGroup);
    }

    @Override
    public CustomerGroupResponse updateCustomerGroup(Long id, CustomerGroupRequest request) {
        log.debug("Updating customer group with id: {}", id);

        CustomerGroup existingGroup = customerGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm khách hàng với ID: " + id));

        if (customerGroupRepository.existsByGroupNameIgnoreCaseAndIdNot(request.getGroupName(), id)) {
            throw new DuplicateResourceException("Tên nhóm khách hàng đã tồn tại: " + request.getGroupName());
        }

        existingGroup.setGroupName(request.getGroupName());
        existingGroup.setDescription(request.getDescription());
        existingGroup.setIsActive(request.getIsActive());

        CustomerGroup updatedGroup = customerGroupRepository.save(existingGroup);
        log.info("Updated customer group with ID: {}", updatedGroup.getId());

        return mapToResponse(updatedGroup);
    }

    @Override
    public void deleteCustomerGroup(Long id) {
        log.debug("Deleting customer group with id: {}", id);

        CustomerGroup customerGroup = customerGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm khách hàng với ID: " + id));

        if (customerGroup.getCustomers() != null && !customerGroup.getCustomers().isEmpty()) {
            throw new IllegalStateException("Không thể xóa nhóm khách hàng đang có khách hàng");
        }

        customerGroupRepository.deleteById(id);
        log.info("Deleted customer group with ID: {}", id);
    }

    @Override
    public Page<CustomerGroupResponse> searchCustomerGroups(String keyword, Pageable pageable) {
        log.debug("Searching customer groups with keyword: {}", keyword);
        Page<CustomerGroup> customerGroups = customerGroupRepository
                .findByGroupNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        return customerGroups.map(this::mapToResponse);
    }

    @Override
    public Long countCustomerGroups() {
        log.debug("Counting total customer groups");
        return customerGroupRepository.count();
    }

    @Override
    public CustomerGroupResponse changeCustomerGroupStatus(Long id, boolean active) {
        log.debug("Changing customer group status for id: {} to: {}", id, active);

        CustomerGroup customerGroup = customerGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm khách hàng với ID: " + id));

        customerGroup.setIsActive(active);
        CustomerGroup updatedGroup = customerGroupRepository.save(customerGroup);

        log.info("Changed customer group status for ID: {} to: {}", id, active);
        return mapToResponse(updatedGroup);
    }

    private CustomerGroupResponse mapToResponse(CustomerGroup customerGroup) {
        return CustomerGroupResponse.builder()
                .id(customerGroup.getId())
                .groupName(customerGroup.getGroupName())
                .description(customerGroup.getDescription())
                .createdAt(customerGroup.getCreatedAt())
                .updatedAt(customerGroup.getUpdatedAt())
                .isActive(customerGroup.getIsActive())
                .customerCount(customerGroup.getCustomers() != null ? (long) customerGroup.getCustomers().size() : 0L)
                .status(customerGroup.getIsActive() ? "Hoạt động" : "Không hoạt động")
                .build();
    }
}
