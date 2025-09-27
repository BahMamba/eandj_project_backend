package com.ejinternational.ej_platform_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ejinternational.ej_platform_backend.model.User;
import com.ejinternational.ej_platform_backend.model.enums.RoleUser;
import com.ejinternational.ej_platform_backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AdminSetupConfig {

    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            String email = "admin@test.com";
            String adminPassword = "1234"; // mot de passe initial

            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = new User();
                admin.setUsername("E&JByAdmin");
                admin.setEmail(email);
                admin.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                admin.setRole(RoleUser.ADMIN);
                admin.setCreatedAt(java.time.LocalDateTime.now());
                admin.setUpdatedAt(java.time.LocalDateTime.now());

                userRepository.save(admin);
                log.info("Admin créé avec succès !");
            } else {
                log.info("Admin déjà existant.");
            }
        };
    }
}
