package com.lakshya.dto.request;

import lombok.Data;

@Data
public class TenantRequest {
    private String name;
    private String email;
    private String planType;
}
