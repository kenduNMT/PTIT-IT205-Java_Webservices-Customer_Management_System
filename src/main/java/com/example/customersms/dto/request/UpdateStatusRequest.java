package com.example.customersms.dto.request;

import com.example.customersms.entity.Customer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UpdateStatusRequest {
    private Customer.Status status;

}