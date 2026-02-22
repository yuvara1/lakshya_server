package com.lakshya.repository;

import com.lakshya.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmailAndTenantId(String email, UUID tenantId);
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findAllByTenantId(UUID tenantId);
    Optional<AppUser> findByIdAndTenantId(UUID id, UUID tenantId);
}
