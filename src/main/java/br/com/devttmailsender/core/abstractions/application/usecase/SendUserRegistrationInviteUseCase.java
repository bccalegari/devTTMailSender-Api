package br.com.devttmailsender.core.abstractions.application.usecase;

public interface SendUserRegistrationInviteUseCase {
    void send(String fullName, String email, String token, String creatorName);
}