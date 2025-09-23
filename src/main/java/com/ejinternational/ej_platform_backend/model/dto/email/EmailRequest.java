package com.ejinternational.ej_platform_backend.model.dto.email;

public record EmailRequest(
    String to,
    String subject,
    String body) {}
