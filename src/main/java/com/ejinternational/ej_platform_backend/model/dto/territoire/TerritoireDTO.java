package com.ejinternational.ej_platform_backend.model.dto.territoire;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TerritoireDTO(
    @NotBlank String nom,
    @NotBlank String polygoneJson,
    @NotNull Long responsableId
) {}

