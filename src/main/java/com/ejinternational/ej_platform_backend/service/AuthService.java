package com.ejinternational.ej_platform_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ejinternational.ej_platform_backend.config.JwtUtil;
import com.ejinternational.ej_platform_backend.model.User;
import com.ejinternational.ej_platform_backend.model.dto.auth.LoginRequest;
import com.ejinternational.ej_platform_backend.model.dto.auth.LoginResponse;
import com.ejinternational.ej_platform_backend.model.dto.auth.RefreshTokenResponse;
import com.ejinternational.ej_platform_backend.model.dto.auth.UserProfileResponse;
import com.ejinternational.ej_platform_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // --- Login ---
    public LoginResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return new LoginResponse(jwtToken, refreshToken, user.getEmail(), user.getRole().name());
    }

    // --- Profil de l'utilisateur ---
    public UserProfileResponse userProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return new UserProfileResponse(user.getUsername(), user.getEmail(), user.getPhoneNumber(), user.getRole());
    }

    // --- Refresh Token ---
    public RefreshTokenResponse refreshToken(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token invalide ou expiré");
        }

        String newAccessToken = jwtUtil.generateToken(userDetails);

        return new RefreshTokenResponse(newAccessToken, refreshToken);
    }
}
