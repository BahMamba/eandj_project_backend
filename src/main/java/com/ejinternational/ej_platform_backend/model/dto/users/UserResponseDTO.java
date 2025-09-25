package com.ejinternational.ej_platform_backend.model.dto.users;

import com.ejinternational.ej_platform_backend.model.enums.RoleUser;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String phoneNumber,
    RoleUser role,
    boolean firstLogin
) {}
