package com.lakshya.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class RegistrationRequest {
    private String name;
    private String email;
    private String password;
    private String role;
    private UUID tenantId;
}
