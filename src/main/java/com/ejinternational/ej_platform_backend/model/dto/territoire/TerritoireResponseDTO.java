package com.ejinternational.ej_platform_backend.model.dto.territoire;


public record TerritoireResponseDTO(
    Long id,
    String nom,
    String polygoneJson,
    String responsable
) {}
