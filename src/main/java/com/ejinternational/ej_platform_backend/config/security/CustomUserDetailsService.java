package com.ejinternational.ej_platform_backend.config.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ejinternational.ej_platform_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(user -> User.withUsername(user.getEmail())
                             .password(user.getPassword())
                             .roles(user.getRole().name()) // Supprimer le préfixe ROLE_
                             .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}