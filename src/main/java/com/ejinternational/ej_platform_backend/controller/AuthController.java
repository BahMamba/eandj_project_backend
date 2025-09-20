package com.ejinternational.ej_platform_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejinternational.ej_platform_backend.model.dto.auth.LoginRequest;
import com.ejinternational.ej_platform_backend.model.dto.auth.LoginResponse;
import com.ejinternational.ej_platform_backend.model.dto.auth.RefreshTokenRequest;
import com.ejinternational.ej_platform_backend.model.dto.auth.RefreshTokenResponse;
import com.ejinternational.ej_platform_backend.model.dto.auth.UserProfileResponse;
import com.ejinternational.ej_platform_backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile() {
        return ResponseEntity.ok(authService.userProfile());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }
}
