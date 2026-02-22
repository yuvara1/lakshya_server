package com.lakshya.service;

import com.lakshya.dto.request.CreateTenantUserRequest;
import com.lakshya.dto.response.EmployeeTenantUserResponse;
import com.lakshya.dto.response.EmployeeTenantUserResponse;
import com.lakshya.entity.AppUser;
import com.lakshya.entity.Tenant;
import com.lakshya.entity.enums.UserRole;
import com.lakshya.repository.AppUserRepository;
import com.lakshya.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AppUserRepository appUserRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeTenantUserResponse createTenantUser(CreateTenantUserRequest request, UUID tenantId) {
        if (appUserRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new RuntimeException("Employee with this email already exists");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        AppUser employee = new AppUser();
        employee.setTenant(tenant);
        employee.setName(request.getFirstName() + " " + request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        employee.setContactNo(request.getContactNo());
        employee.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        employee.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        employee.setCreatedAt(Instant.now());

        AppUser saved = appUserRepository.save(employee);
        return mapToResponse(saved);
    }

    public List<EmployeeTenantUserResponse> getAllTenantUser(UUID tenantId) {
        return appUserRepository.findAllByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTenantUser(UUID employeeId, UUID tenantId) {
        AppUser employee = appUserRepository.findByIdAndTenantId(employeeId, tenantId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        appUserRepository.delete(employee);
    }

    private EmployeeTenantUserResponse mapToResponse(AppUser user) {
        return EmployeeTenantUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNo(user.getContactNo())
                .role(user.getRole().name())
                .status(user.getStatus())
                .build();
    }
}