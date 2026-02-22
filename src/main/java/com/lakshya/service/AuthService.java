package com.lakshya.service;

import com.lakshya.dto.request.LoginRequest;
import com.lakshya.dto.request.OrganizationRegistrationRequest;
import com.lakshya.dto.request.RegistrationRequest;
import com.lakshya.dto.response.AuthResponse;
import com.lakshya.entity.AppUser;
import com.lakshya.entity.RefreshToken;
import com.lakshya.entity.Tenant;
import com.lakshya.entity.enums.PlanType;
import com.lakshya.entity.enums.UserRole;
import com.lakshya.repository.AppUserRepository;
import com.lakshya.repository.TenantRepository;
import com.lakshya.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse registerOrganization(OrganizationRegistrationRequest request) {
        if (tenantRepository.existsByDomain(request.getDomain())) {
            throw new RuntimeException("Organization with this domain already exists");
        }

        AppUser adminUser = new AppUser();
        adminUser.setName(request.getFirstName() + " " + request.getLastName());
        adminUser.setEmail(request.getEmail());
        adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        adminUser.setContactNo(request.getContactNo());
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus("ACTIVE");
        adminUser.setCreatedAt(Instant.now());

        AppUser savedUser = appUserRepository.save(adminUser);

        Tenant tenant = new Tenant();
        tenant.setName(request.getCompanyName());
        tenant.setEmail(request.getEmail());
        tenant.setDomain(request.getDomain());
        tenant.setContactNo(request.getContactNo() != null ? request.getContactNo() : "");
        tenant.setAddress(request.getAddress());
        tenant.setAdminUser(savedUser);
        tenant.setPlanType(PlanType.FREE);
        tenant.setIsActive(true);
        Tenant savedTenant = tenantRepository.save(tenant);

        savedUser.setTenant(savedTenant);
        appUserRepository.save(savedUser);

        String token = jwtUtil.generateToken(
                savedUser.getId().toString(),
                savedTenant.getId().toString(),
                savedUser.getRole().name()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().name())
                .tenantId(savedTenant.getId().toString())
                .build();
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (appUserRepository.existsByEmailAndTenantId(request.getEmail(), request.getTenantId())) {
            throw new RuntimeException("User already exists with this email");
        }

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        AppUser user = new AppUser();
        user.setTenant(tenant);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());

        AppUser savedUser = appUserRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getId().toString(),
                savedUser.getTenant().getId().toString(),
                savedUser.getRole().name()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().name())
                .tenantId(savedUser.getTenant().getId().toString())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getTenant().getId().toString(),
                user.getRole().name()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantId(user.getTenant().getId().toString())
                .build();
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenStr);
        AppUser user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateToken(
                user.getId().toString(),
                user.getTenant().getId().toString(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshTokenStr)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantId(user.getTenant().getId().toString())
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    public AuthResponse getCurrentUser(String token) {
        Claims claims = jwtUtil.extractClaims(token);
        String userId = claims.getSubject();

        AppUser user = appUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantId(user.getTenant().getId().toString())
                .build();
    }
}
