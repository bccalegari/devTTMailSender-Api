package br.com.devttmailsender.core.application.usecase;

import br.com.devttmailsender.core.abstractions.application.service.MailSenderService;
import br.com.devttmailsender.core.domain.valueobject.Mail;
import br.com.devttmailsender.core.domain.valueobject.MailText;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringSendUserRegistrationInviteUseCaseUnitTest {
    @InjectMocks private SpringSendUserRegistrationInviteUseCase springSendUserRegistrationInviteUseCase;
    @Mock private TemplateEngine templateEngine;
    @Mock private MailSenderService mailService;
    @Mock private Environment env;

    @Test
    void shouldSendUserRegistrationInvite() {
        String fullName = "fullName";
        String email = "email";
        String token = "token";
        String creatorName = "creatorName";
        String html = "html";
        String baseUrl = "baseUrl";
        String registrationPath = "/registrationPath?token=";

        Mail inviteMail = Mail.builder()
                .from("devtt@devtt.com")
                .to(email)
                .subject("Complete a Ativação da Sua Conta no devTT")
                .text(new MailText(html, true))
                .build();

        when(env.getProperty("APP_CLIENT_URL")).thenReturn(baseUrl);
        when(env.getProperty("APP_CLIENT_CONFIRM_INVITE_PATH")).thenReturn(registrationPath);

        try (MockedConstruction<Context> mockedContext = mockConstruction(Context.class,
                (mock, context) -> when(templateEngine.process("user-registration-invitation", mock))
                        .thenReturn(html))) {
            doNothing().when(mailService).send(inviteMail);

            springSendUserRegistrationInviteUseCase.send(fullName, email, token, creatorName);

            var capturedContext = mockedContext.constructed().getFirst();
            verify(capturedContext).setVariable("username", fullName);
            verify(capturedContext).setVariable("creatorName", creatorName);
            verify(capturedContext).setVariable("registrationLink", baseUrl + registrationPath + token);
            verify(templateEngine).process("user-registration-invitation", capturedContext);
            verify(mailService).send(inviteMail);
        }
    }
}