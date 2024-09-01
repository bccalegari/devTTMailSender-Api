package br.com.devttmailsender.infrastructure.configuration.mail;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;

@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MailSenderConfig {
    private final Environment environment;

    @Bean
    public JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environment.getProperty("MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("MAIL_PORT"))));
        mailSender.setUsername(environment.getProperty("MAIL_USERNAME"));
        mailSender.setPassword(environment.getProperty("MAIL_PASSWORD"));

        var props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));

        return mailSender;
    }
}