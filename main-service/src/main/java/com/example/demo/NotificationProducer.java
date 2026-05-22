package com.example.demo;

import com.example.demo.dto.NotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBorrowNotification(NotificationMessage message) {
        System.out.println("Sending to Rabbit: " + message.getContent());

        rabbitTemplate.convertAndSend(RabbitConfig.BORROW_QUEUE, message);
    }

    public void sendReturnNotification(NotificationMessage message) {

        rabbitTemplate.convertAndSend(RabbitConfig.RETURN_QUEUE, message);
    }

    public void sendAvailableNotification(NotificationMessage message) {

        rabbitTemplate.convertAndSend(RabbitConfig.AVAILABLE_QUEUE, message);
    }
}