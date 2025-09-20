package com.ejinternational.ej_platform_backend.model.dto.auth;

public record LoginResponse(String accessToken, String refreshToken, String email, String role) {}
