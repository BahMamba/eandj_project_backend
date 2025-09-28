package com.ejinternational.ej_platform_backend.model.dto.territoire;

import java.time.LocalDateTime;

public record TerritoireResponseDTO(
    Long id,
    String nom,
    String polygoneJson,
    String responsable,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
