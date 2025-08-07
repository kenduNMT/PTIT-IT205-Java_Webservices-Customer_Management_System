package com.example.customersms.dto.request;

import com.example.customersms.entity.User.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private UserStatus status;
}