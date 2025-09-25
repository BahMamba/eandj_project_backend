package com.ejinternational.ej_platform_backend.service;

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

    /* Mapping */
    private TerritoireResponseDTO mapResponseDTO(Territoire territoire){
        return new TerritoireResponseDTO(
            territoire.getId(),
            territoire.getNom(),
            territoire.getPolygoneJson(),
            territoire.getResponsableTerritoire().getId(),
            territoire.getCreatedAt(),
            territoire.getUpdatedAt()
        );
    }

    // Ajout
    public TerritoireResponseDTO createTerritoire(TerritoireDTO dto){

        User commercial = userRepository.findById(dto.responsableId())
                .orElseThrow(() -> new RuntimeException("Commercial introuvable"));

        if (territoireRepository.existsByResponsableTerritoireId(dto.responsableId())) {
            throw new IllegalStateException("Ce commercial est déjà assigner a un territoire");
        }

        Territoire territoire = Territoire.builder()
                    .nom(dto.nom())
                    .polygoneJson(dto.polygoneJson())
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
            throw new IllegalStateException("Ce commercial est déjà assigner a un territoire");
        }

        territoire.setNom(dto.nom());
        territoire.setPolygoneJson(dto.polygoneJson());
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
