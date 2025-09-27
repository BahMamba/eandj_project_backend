package com.ejinternational.ej_platform_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ejinternational.ej_platform_backend.model.Territoire;

public interface TerritoireRepository extends JpaRepository<Territoire, Long> {
    boolean existsByResponsableTerritoireId(Long id);

    Page<Territoire> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Territoire> findByNomContainingIgnoreCaseAndResponsableTerritoireId(String nom, Long responsableId, Pageable pageable);

    Page<Territoire> findByResponsableTerritoireId(Long responsableId, Pageable pageable);
}