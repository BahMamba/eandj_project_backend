package com.ejinternational.ej_platform_backend.service;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.stereotype.Service;

import com.ejinternational.ej_platform_backend.model.Territoire;
import com.ejinternational.ej_platform_backend.model.User;
import com.ejinternational.ej_platform_backend.model.dto.territoire.TerritoireDTO;
import com.ejinternational.ej_platform_backend.model.dto.territoire.TerritoireResponseDTO;
import com.ejinternational.ej_platform_backend.repository.TerritoireRepository;
import com.ejinternational.ej_platform_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class TerritoireService {
    private final UserRepository userRepository;
    private final TerritoireRepository territoireRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    /** Convertit un Polygon -> GeoJSON String */
    private String polygonToGeoJson(Polygon polygon) {
        if (polygon == null) return null;
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(polygon);
    }

    /** Convertit un GeoJSON String -> Polygon */
    private Polygon geoJsonToPolygon(String geoJson) {
        try {
            GeoJsonReader reader = new GeoJsonReader(geometryFactory);
            return (Polygon) reader.read(geoJson);
        } catch (Exception e) {
            throw new RuntimeException("Format GeoJSON invalide", e);
        }
    }

    /* Mapping */
    private TerritoireResponseDTO mapResponseDTO(Territoire territoire){
        return new TerritoireResponseDTO(
            territoire.getId(),
            territoire.getNom(),
            polygonToGeoJson(territoire.getPolygone()), // ✅ conversion
            territoire.getResponsableTerritoire() != null ? territoire.getResponsableTerritoire().getUsername() : "N/A",
            territoire.getCreatedAt(),
            territoire.getUpdatedAt()
        );
    }

    // Ajout
    public TerritoireResponseDTO createTerritoire(TerritoireDTO dto){
        User commercial = userRepository.findById(dto.responsableId())
                .orElseThrow(() -> new RuntimeException("Commercial introuvable"));

        if (territoireRepository.existsByResponsableTerritoireId(dto.responsableId())) {
            throw new IllegalStateException("Ce commercial est déjà assigné à un territoire");
        }

        Territoire territoire = Territoire.builder()
                .nom(dto.nom())
                .polygone(geoJsonToPolygon(dto.polygoneJson())) // ✅ conversion
                .responsableTerritoire(commercial)
                .build();

        territoire = territoireRepository.save(territoire);
        return mapResponseDTO(territoire);
    }

    // Update
    public TerritoireResponseDTO updateTerritoire(Long id, TerritoireDTO dto) {
        Territoire territoire = territoireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Territoire introuvable"));

        User commercial = userRepository.findById(dto.responsableId())
                .orElseThrow(() -> new RuntimeException("Commercial introuvable"));

        if (!territoire.getResponsableTerritoire().getId().equals(dto.responsableId()) &&
            territoireRepository.existsByResponsableTerritoireId(dto.responsableId())) {
            throw new IllegalStateException("Ce commercial est déjà assigné à un territoire");
        }

        territoire.setNom(dto.nom());
        territoire.setPolygone(geoJsonToPolygon(dto.polygoneJson())); // ✅ conversion
        territoire.setResponsableTerritoire(commercial);

        territoire = territoireRepository.save(territoire);
        return mapResponseDTO(territoire);
    }

    // Get by ID
    public TerritoireResponseDTO getTerritoireById(Long id) {
        Territoire territoire = territoireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Territoire introuvable"));
        return mapResponseDTO(territoire);
    }

    // Delete
    public void deleteTerritoire(Long id) {
        if (!territoireRepository.existsById(id)) {
            throw new RuntimeException("Territoire introuvable");
        }
        territoireRepository.deleteById(id);
    }

    // Pagination & filtres
    public Page<TerritoireResponseDTO> getAllTerritoires(String nom, Long responsableId, Pageable pageable) {
        Page<Territoire> territoires;

        if (nom != null && !nom.isEmpty() && responsableId != null) {
            territoires = territoireRepository.findByNomContainingIgnoreCaseAndResponsableTerritoireId(nom, responsableId, pageable);
        } else if (nom != null && !nom.isEmpty()) {
            territoires = territoireRepository.findByNomContainingIgnoreCase(nom, pageable);
        } else if (responsableId != null) {
            territoires = territoireRepository.findByResponsableTerritoireId(responsableId, pageable);
        } else {
            territoires = territoireRepository.findAll(pageable);
        }

        return territoires.map(this::mapResponseDTO);
    }
}
