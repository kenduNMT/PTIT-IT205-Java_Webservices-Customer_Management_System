package com.example.customersms.service;

import com.example.customersms.dto.request.CustomerGroupRequest;
import com.example.customersms.dto.response.CustomerGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerGroupService {

    Page<CustomerGroupResponse> getAllCustomerGroups(Pageable pageable);

    List<CustomerGroupResponse> getAllCustomerGroupsList();

    CustomerGroupResponse getCustomerGroupById(Long id);

    CustomerGroupResponse createCustomerGroup(CustomerGroupRequest request);

    CustomerGroupResponse updateCustomerGroup(Long id, CustomerGroupRequest request);

    void deleteCustomerGroup(Long id);

    Page<CustomerGroupResponse> searchCustomerGroups(String keyword, Pageable pageable);

    Long countCustomerGroups();

    CustomerGroupResponse changeCustomerGroupStatus(Long id, boolean active);
}
