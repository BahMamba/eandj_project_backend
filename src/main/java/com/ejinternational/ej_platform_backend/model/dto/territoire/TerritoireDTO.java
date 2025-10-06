package com.ejinternational.ej_platform_backend.model.dto.territoire;

import jakarta.validation.constraints.NotBlank;

public record TerritoireDTO(
    Long responsableId,
    @NotBlank String nom,
    @NotBlank String polygoneJson,
    @NotBlank String responsable
) {}

