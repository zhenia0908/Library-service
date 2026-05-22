package com.example.demo.service;

import com.example.demo.dto.NotificationMessage;
import com.example.demo.dto.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    @RabbitListener(queues = RabbitConfig.BORROW_QUEUE)
    public void borrowNotification( NotificationMessage message) {

        System.out.println("BORROW: " + message.getContent());
    }


    @RabbitListener(queues = RabbitConfig.RETURN_QUEUE)
    public void returnNotification(NotificationMessage message) {

        System.out.println("RETURN: " + message.getContent());
    }


    @RabbitListener(queues = RabbitConfig.AVAILABLE_QUEUE)
    public void availableNotification(NotificationMessage message) {

        System.out.println("AVAILABLE: " + message.getContent());
    }
}