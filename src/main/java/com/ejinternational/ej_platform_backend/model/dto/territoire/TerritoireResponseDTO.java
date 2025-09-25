package com.ejinternational.ej_platform_backend.model.dto.territoire;

import java.time.LocalDateTime;

public record TerritoireResponseDTO(
    Long id,
    String nom,
    String polygoneJson,
    Long responsableId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
