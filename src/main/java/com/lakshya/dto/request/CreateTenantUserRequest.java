package com.lakshya.dto.request;

import lombok.Data;

@Data
public class CreateTenantUserRequest {
    private String tenantId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String contactNo;
    private String role;
    private String status;
}
