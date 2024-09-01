package br.com.devttmailsender.infrastructure.adapters.dto;

public record UserRegistrationInviteDto(
        String fullName,
        String email,
        String token,
        String creatorName
) {}