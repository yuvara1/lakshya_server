package com.lakshya.service;

import com.lakshya.dto.request.TenantRequest;
import com.lakshya.dto.response.TenantResponse;
import com.lakshya.entity.Tenant;
import com.lakshya.entity.enums.PlanType;
import com.lakshya.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantResponse createTenant(TenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setEmail(request.getEmail());
        tenant.setPlanType(PlanType.valueOf(request.getPlanType().toUpperCase()));
        tenant.setIsActive(true);

        Tenant saved = tenantRepository.save(tenant);
        return mapToResponse(saved);
    }

    public TenantResponse getTenant(UUID id) {
        Tenant tenant = (Tenant) tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return mapToResponse(tenant);
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TenantResponse mapToResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .email(tenant.getEmail())
                .planType(tenant.getPlanType().name())
                .isActive(tenant.getIsActive())
                .build();
    }
}
