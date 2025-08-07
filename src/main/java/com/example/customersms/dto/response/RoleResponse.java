package com.example.customersms.dto.response;

import com.example.customersms.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private Roles.RoleName name;
    private String description;

    public RoleResponse(Roles role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
    }
}