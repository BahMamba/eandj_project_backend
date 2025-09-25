package com.ejinternational.ej_platform_backend.config;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long TOKEN_EXPIRATION = 86400000L;       // 24h
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 jours

    public String generateToken(UserDetails userDetails) {
        log.debug("Génération d’un token pour l’utilisateur {}", userDetails.getUsername());
        return buildToken(userDetails, TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("Génération d’un refresh token pour l’utilisateur {}", userDetails.getUsername());
        return buildToken(userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    public String extractUsername(String token) {
        try {
            String username = getClaims(token).getSubject();
            log.trace("Extraction du username depuis le token : {}", username);
            return username;
        } catch (Exception e) {
            log.error("Erreur lors de l’extraction du username du token", e);
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean valid = username.equals(userDetails.getUsername()) && !isExpiredToken(token);
            log.debug("Validation du token pour utilisateur {} : {}", username, valid ? "valide" : "invalide");
            return valid;
        } catch (Exception e) {
            log.warn("Échec de validation du token", e);
            return false;
        }
    }

    private boolean isExpiredToken(String token) {
        boolean expired = getClaims(token).getExpiration().before(new Date());
        if (expired) {
            log.info("Token expiré (exp: {})", getClaims(token).getExpiration());
        }
        return expired;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        String token = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
                .compact();

        log.trace("Token généré pour {} avec expiration {}", userDetails.getUsername(), expiration);
        return token;
    }

    public String getSecret() {
        return secret;
    }
}
