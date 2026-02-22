package com.lakshya.controller;

import com.lakshya.dto.request.CreateTenantUserRequest;
import com.lakshya.dto.response.EmployeeTenantUserResponse;
import com.lakshya.dto.response.EmployeeTenantUserResponse;
import com.lakshya.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/{tenantId}/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<EmployeeTenantUserResponse> createTenantUser(
            @RequestBody CreateTenantUserRequest request,
            HttpServletRequest httpRequest, @PathVariable String tenantId) {

        logger.info("Admin creating employee: {}", request.getEmail());
        logger.info("Creating employee: {}", request);

        EmployeeTenantUserResponse response = adminService.createTenantUser(request, UUID.fromString(tenantId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<EmployeeTenantUserResponse>> getAllUsers(@PathVariable String tenantId) {
        List<EmployeeTenantUserResponse> employees = adminService.getAllTenantUser(UUID.fromString(tenantId));
        return ResponseEntity.ok(employees);
    }



    @GetMapping("/user/{id}")
    public ResponseEntity<EmployeeTenantUserResponse> getUserById(@PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = (String) httpRequest.getAttribute("tenantId");
        List<EmployeeTenantUserResponse> employees = adminService.getAllTenantUser(UUID.fromString(tenantId));
        EmployeeTenantUserResponse employee = employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteTenantUser(@PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = (String) httpRequest.getAttribute("tenantId");
        adminService.deleteTenantUser(id, UUID.fromString(tenantId));
        return ResponseEntity.noContent().build();
    }

    // update user
    @PutMapping("/user/{id}")
    public ResponseEntity<EmployeeTenantUserResponse> updateUser(@PathVariable UUID id, @RequestBody CreateTenantUserRequest request, HttpServletRequest httpRequest) {
        String tenantId = (String) httpRequest.getAttribute("tenantId");
        adminService.deleteTenantUser(id, UUID.fromString(tenantId));
        EmployeeTenantUserResponse response = adminService.createTenantUser(request, UUID.fromString(tenantId));
        return ResponseEntity.ok(response);
    }
}
