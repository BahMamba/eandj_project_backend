package com.ejinternational.ej_platform_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ejinternational.ej_platform_backend.model.dto.territoire.TerritoireDTO;
import com.ejinternational.ej_platform_backend.model.dto.territoire.TerritoireResponseDTO;
import com.ejinternational.ej_platform_backend.service.TerritoireService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/territoires")
public class TerritoireController {

    private final TerritoireService territoireService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TerritoireResponseDTO> createTerritoire(@RequestBody TerritoireDTO dto) {
        TerritoireResponseDTO response = territoireService.createTerritoire(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TerritoireResponseDTO> updateTerritoire(@PathVariable Long id, @RequestBody TerritoireDTO dto) {
        TerritoireResponseDTO response = territoireService.updateTerritoire(id, dto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TerritoireResponseDTO> getTerritoireById(@PathVariable Long id) {
        TerritoireResponseDTO response = territoireService.getTerritoireById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerritoire(@PathVariable Long id) {
        territoireService.deleteTerritoire(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TerritoireResponseDTO>> getAllTerritoires(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Long responsableId,
            Pageable pageable) {
        Page<TerritoireResponseDTO> page = territoireService.getAllTerritoires(nom, responsableId, pageable);
        return ResponseEntity.ok(page);
    }
}
