package com.ejinternational.ej_platform_backend.model.dto.users;

import com.ejinternational.ej_platform_backend.model.enums.RoleUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
    @NotBlank(message = "Username is required")
    String username,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    @Size(min = 9, max = 13, message = "Phone number must be between 9 and 13 characters")
    String phoneNumber,

    RoleUser role
) {}
