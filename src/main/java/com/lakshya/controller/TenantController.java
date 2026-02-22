package com.lakshya.controller;

import com.lakshya.dto.request.TenantRequest;
import com.lakshya.dto.response.TenantResponse;
import com.lakshya.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody TenantRequest request) {
        logger.info("Received tenant creation request for: {}", request.getName());
        logger.debug("Tenant details - Email: {}, PlanType: {}", request.getEmail(), request.getPlanType());

        TenantResponse response = tenantService.createTenant(request);

        logger.info("Tenant created successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable UUID id) {
        logger.info("Fetching tenant with ID: {}", id);

        TenantResponse response = tenantService.getTenant(id);

        logger.debug("Retrieved tenant: {}", response.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        logger.info("Fetching all tenants");

        List<TenantResponse> tenants = tenantService.getAllTenants();

        logger.info("Retrieved {} tenants", tenants.size());
        return ResponseEntity.ok(tenants);
    }
}