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

@Component
public class JwtUtil {
    @Value(value = "${jwt.secret}")
    private String secret;

    private final long tokenExp = 3600000L;
    private final long refTokenExp = 604800000L; 
    
    public String generateToken(UserDetails userDetails){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + tokenExp))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
            .compact();
    }

    public String generateRefreshToken(UserDetails userDetails){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refTokenExp))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
            .compact();
    }

    public String extractUsername(String token){
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            System.out.println("Erreur extraction username: " + e.getMessage()); // Log temporaire
            throw e;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            System.out.println("Erreur parsing claims: " + e.getMessage()); // Log temporaire
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails){
        try {
            String username = extractUsername(token);
            boolean isExpired = isExpiredToken(token);
            boolean isValidUsername = username.equals(userDetails.getUsername());
            System.out.println("Validation token - Username: " + username + ", Valid: " + isValidUsername + ", Expired: " + isExpired); // Log temporaire
            return isValidUsername && !isExpired;
        } catch (Exception e) {
            System.out.println("Erreur validation token: " + e.getMessage()); // Log temporaire
            return false;
        }
    }

    private boolean isExpiredToken(String token){
        try {
            boolean isExpired = getClaims(token).getExpiration().before(new Date());
            System.out.println("Token expiration: " + getClaims(token).getExpiration() + ", Is expired: " + isExpired); // Log temporaire
            return isExpired;
        } catch (Exception e) {
            System.out.println("Erreur vérification expiration: " + e.getMessage()); // Log temporaire
            return true;
        }
    }

    public String getSecret() {
        return secret;
    }
}