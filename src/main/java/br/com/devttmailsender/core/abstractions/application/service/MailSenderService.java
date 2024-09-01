package br.com.devttmailsender.core.abstractions.application.service;

import br.com.devttmailsender.core.domain.valueobject.Mail;

public interface MailSenderService {
    void send(Mail mail);
}