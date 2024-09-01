package br.com.devttmailsender.core.application.service;

import br.com.devttmailsender.core.abstractions.application.service.MailSenderService;
import br.com.devttmailsender.core.domain.valueobject.Mail;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Qualifier("MailSenderServiceImpl")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;

    @Override
    public void send(Mail mail) {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getText().text(), mail.getText().isHtml());
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email message to: {} from: {} with subject: {} and text: {}",
                    mail.getTo(), mail.getFrom(), mail.getSubject(), mail.getText().text(), e);
        }
    }
}