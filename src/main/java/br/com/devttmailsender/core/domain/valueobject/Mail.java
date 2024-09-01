package br.com.devttmailsender.core.domain.valueobject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Mail {
    private String from;
    private String to;
    private String subject;
    private MailText text;
}