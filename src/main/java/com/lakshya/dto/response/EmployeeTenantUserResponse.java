package com.lakshya.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployeeTenantUserResponse {
    private UUID id;
    private String name;
    private String email;
    private String contactNo;
    private String role;
    private String status;
}