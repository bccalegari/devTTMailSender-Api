package br.com.devttmailsender.infrastructure.adapters.consumer;

import br.com.devttmailsender.core.abstractions.application.usecase.SendUserRegistrationInviteUseCase;
import br.com.devttmailsender.infrastructure.adapters.dto.UserRegistrationInviteDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RabbitConsumer {
    private final SendUserRegistrationInviteUseCase sendUserRegistrationInviteUseCase;

    @Autowired
    public RabbitConsumer(
            @Qualifier("SpringSendUserRegistrationInviteUseCase")
            SendUserRegistrationInviteUseCase sendUserRegistrationInviteUseCase
    ) {
        this.sendUserRegistrationInviteUseCase = sendUserRegistrationInviteUseCase;
    }

    @RabbitListener(queues = "mail-queue")
    public void consume(Message message, Channel channel) {
        try {
            log.info("Message received: {}", message);
            var messageBody = new String(message.getBody());

            var dto = parseMessage(messageBody);
            processMessage(dto);

            log.info("Message processed: {}", message);
        } catch (Exception e) {
            handleException(e, message, channel);
        }
    }

    private UserRegistrationInviteDto parseMessage(String messageBody) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(messageBody, UserRegistrationInviteDto.class);
    }

    private void processMessage(UserRegistrationInviteDto dto) {
        sendUserRegistrationInviteUseCase.send(dto.fullName(), dto.email(), dto.token(), dto.creatorName());
    }

    private void handleException(Exception e, Message message, Channel channel) {
        log.error(e.getMessage(), e);
        log.error("Error processing message: {}", message);
        try {
            rejectMessage(message, channel);
        } catch (Exception ex) {
            log.error(e.getMessage(), e);
            log.error("Error rejecting message: {}", message);
        }
    }

    private void rejectMessage(Message message, Channel channel) throws IOException {
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        log.info("Message rejected: {}", message);
    }
}