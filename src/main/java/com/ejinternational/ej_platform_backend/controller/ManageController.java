package com.ejinternational.ej_platform_backend.controller;

import com.ejinternational.ej_platform_backend.model.dto.users.UserDTO;
import com.ejinternational.ej_platform_backend.model.dto.users.UserPasswordDTO;
import com.ejinternational.ej_platform_backend.model.dto.users.UserResponseDTO;
import com.ejinternational.ej_platform_backend.service.ManageService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ManageController {

    private final ManageService manageService;

    @PostMapping("/commercial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createCommercial(@RequestBody UserDTO userDTO) {
        UserResponseDTO createdUser = manageService.createCommercial(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDTO> getAllUsers(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return manageService.getAllUsers(keyword, page, size);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(manageService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(manageService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        manageService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/first-login")
    public ResponseEntity<UserResponseDTO> firstLoginChange(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UserPasswordDTO dto
    ){
        Long userId = manageService.getUserByEmail(userDetails.getUsername()).getId();
        UserResponseDTO updatedUser = manageService.firstLoginChange(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }
}
