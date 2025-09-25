package com.ejinternational.ej_platform_backend.service;

import com.ejinternational.ej_platform_backend.model.User;
import com.ejinternational.ej_platform_backend.model.dto.users.UserDTO;
import com.ejinternational.ej_platform_backend.model.dto.users.UserPasswordDTO;
import com.ejinternational.ej_platform_backend.model.dto.users.UserResponseDTO;
import com.ejinternational.ej_platform_backend.model.enums.RoleUser;
import com.ejinternational.ej_platform_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // 🔹 Mapper interne : User -> UserResponseDTO
    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getFirstLogin()
        );
    }

    // Methode pour recuperer un user a travers l'email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
    }


    // Ajouter un commercial
    public UserResponseDTO createCommercial(UserDTO userDTO) {
        String genPassword = UUID.randomUUID().toString().substring(0, 6);
        String encodedPassword = passwordEncoder.encode(genPassword);

        User user = User.builder()
                .username(userDTO.username())
                .email(userDTO.email())
                .phoneNumber(userDTO.phoneNumber())
                .role(RoleUser.COMMERCIAL)
                .firstLogin(true)
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        // Envoi des credentials par email
        emailService.sendCommercialCredentials(
                user.getEmail(),
                user.getUsername(),
                user.getEmail(),
                genPassword
        );

        return mapToResponseDTO(user);
    }

    // Récupérer tous les utilisateurs
    public Page<UserResponseDTO> getAllUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> usersPage;

        if (keyword == null || keyword.isBlank()) {
            usersPage = userRepository.findAll(pageable);
        } 
        else {
            usersPage = userRepository
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContaining(
                            keyword, keyword, keyword, pageable);
        }

        return usersPage.map(this::mapToResponseDTO);
    }

    // Récupérer un utilisateur par ID
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
    }

    // Modifier un utilisateur (sauf mot de passe ici)
    public UserResponseDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));

        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPhoneNumber(userDTO.phoneNumber());

        userRepository.save(user);
        return mapToResponseDTO(user);
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + id);
        }
        userRepository.deleteById(id);
    }


    // Service : ManageService.java
    public UserResponseDTO firstLoginChange(Long userId, UserPasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        // Vérification que l'utilisateur est encore en first login
        if (!user.getFirstLogin()) {
            throw new IllegalStateException("Mot de passe déjà modifié, action interdite.");
        }

        // Vérification du mot de passe temporaire
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }

        // Vérification correspondance nouveau mot de passe
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et sa confirmation ne correspondent pas");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));

        user.setFirstLogin(false);

        userRepository.save(user);

        emailService.sendPasswordChangedConfirmation(user.getEmail(), user.getUsername());

        return mapToResponseDTO(user);
    }

}
