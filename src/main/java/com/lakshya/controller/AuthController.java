package com.lakshya.controller;

import com.lakshya.dto.request.LoginRequest;
import com.lakshya.dto.request.OrganizationRegistrationRequest;
import com.lakshya.dto.request.RefreshTokenRequest;
import com.lakshya.dto.request.RegistrationRequest;
import com.lakshya.dto.response.AuthResponse;
import com.lakshya.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register-organization")
    public ResponseEntity<AuthResponse> registerOrganization(@RequestBody OrganizationRegistrationRequest request) {
        logger.info("Received organization registration request for company: {}", request.getCompanyName());
        AuthResponse response = authService.registerOrganization(request);
        logger.info("Organization registered successfully for: {}", request.getCompanyName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest request) {
        logger.info("Received user registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        logger.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, AuthResponse>> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        logger.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(Map.of("user", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        logger.info("Refresh token request received");
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
        logger.info("Logout request received");
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        AuthResponse response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }
}
