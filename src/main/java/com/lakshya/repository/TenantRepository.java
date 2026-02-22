package com.lakshya.repository;

import com.lakshya.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    boolean existsByName(String name);
    Optional<Tenant> findByEmail(String name);
    boolean existsByDomain(String domain);
    boolean existsByEmail(String tenantEmail);

    Optional<Tenant> findById(UUID tenantId);
}
