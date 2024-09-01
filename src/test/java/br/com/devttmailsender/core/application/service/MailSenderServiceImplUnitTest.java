package br.com.devttmailsender.core.application.service;

import br.com.devttmailsender.core.domain.valueobject.Mail;
import br.com.devttmailsender.core.domain.valueobject.MailText;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailSenderServiceImplUnitTest {
    @InjectMocks private MailSenderServiceImpl mailSenderService;
    @Mock private JavaMailSender mailSender;

    @Test
    void shouldSendMail() {
        var mail = Mail.builder()
                .from("from")
                .to("to")
                .subject("subject")
                .text(new MailText("text", true))
                .build();

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                (mock, context) -> when(mock.getMimeMessage()).thenReturn(mimeMessage))) {
            mailSenderService.send(mail);

            MimeMessageHelper mimeMessageHelper = mocked.constructed().getFirst();
            verify(mimeMessageHelper).setFrom(mail.getFrom());
            verify(mimeMessageHelper).setTo(mail.getTo());
            verify(mimeMessageHelper).setSubject(mail.getSubject());
            verify(mimeMessageHelper).setText(mail.getText().text(), mail.getText().isHtml());
            verify(mailSender).send(mimeMessage);

            verifyNoMoreInteractions(mailSender);
        } catch (MessagingException e) {
            fail("Test failed due to MessagingException: " + e.getMessage());
        }
    }
}