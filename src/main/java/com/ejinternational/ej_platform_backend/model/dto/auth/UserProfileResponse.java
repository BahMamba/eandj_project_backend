package com.ejinternational.ej_platform_backend.model.dto.auth;

import com.ejinternational.ej_platform_backend.model.enums.RoleUser;

public record UserProfileResponse(String username, String email, String phoneNumber, RoleUser roleUser) {}