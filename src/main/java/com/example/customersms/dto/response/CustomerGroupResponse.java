package com.example.customersms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerGroupResponse {

    private Long id;

    private String groupName;

    private String description;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    private Boolean isActive;

    private Long customerCount; // Số lượng khách hàng trong nhóm

    private String status; // Trạng thái hiển thị: "Hoạt động" / "Không hoạt động"
}