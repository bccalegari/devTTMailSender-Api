package br.com.devttmailsender.core.application.usecase;

import br.com.devttmailsender.core.abstractions.application.service.MailSenderService;
import br.com.devttmailsender.core.abstractions.application.usecase.SendUserRegistrationInviteUseCase;
import br.com.devttmailsender.core.domain.valueobject.Mail;
import br.com.devttmailsender.core.domain.valueobject.MailText;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Qualifier("SpringSendUserRegistrationInviteUseCase")
public class SpringSendUserRegistrationInviteUseCase
        implements SendUserRegistrationInviteUseCase {
    private final TemplateEngine templateEngine;
    private final MailSenderService mailService;
    private final Environment env;

    public SpringSendUserRegistrationInviteUseCase(
            TemplateEngine templateEngine,
            @Qualifier("MailSenderServiceImpl") MailSenderService mailService,
            Environment env
    ) {
        this.templateEngine = templateEngine;
        this.mailService = mailService;
        this.env = env;
    }

    @Override
    public void send(String fullName, String email, String token, String creatorName) {
        var context = new Context();
        context.setVariable("username", fullName);
        context.setVariable("creatorName", creatorName);
        context.setVariable("registrationLink", buildRegistrationLink(token));
        var html = templateEngine.process("user-registration-invitation", context);

        var inviteEmail = Mail.builder()
                .from("devtt@devtt.com")
                .to(email)
                .subject("Complete a Ativação da Sua Conta no devTT")
                .text(new MailText(html, true))
                .build();
        mailService.send(inviteEmail);
    }

    private String buildRegistrationLink(String token) {
        String baseUrl = env.getProperty("APP_CLIENT_URL");
        String registrationPath = env.getProperty("APP_CLIENT_CONFIRM_INVITE_PATH");
        return baseUrl + registrationPath + token;
    }
}