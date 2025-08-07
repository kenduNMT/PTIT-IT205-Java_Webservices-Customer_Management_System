package com.example.customersms.dto.request;

import com.example.customersms.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    private Roles.RoleName name;
    private String description;
}