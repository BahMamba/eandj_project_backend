package com.ejinternational.ej_platform_backend.model.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordDTO(
    @NotBlank(message = "Le mot de passe actuel est requis")
    String oldPassword,

    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères")
    String newPassword,

    @NotBlank(message = "La confirmation du nouveau mot de passe est requise")
    String confirmPassword
) {}

