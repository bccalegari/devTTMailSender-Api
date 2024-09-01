package br.com.devttmailsender.core.domain.valueobject;

public record MailText(
        String text,
        Boolean isHtml
) {}