package com.lakshya.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TenantResponse {
    private UUID id;
    private String name;
    private String email;
    private String planType;
    private Boolean isActive;
}
