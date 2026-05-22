package com.example.demo;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String BORROW_QUEUE = "borrow.queue";
    public static final String RETURN_QUEUE = "return.queue";
    public static final String AVAILABLE_QUEUE = "available.queue";

    @Bean
    public Queue borrowQueue() {
        return new Queue(BORROW_QUEUE, true);
    }

    @Bean
    public Queue returnQueue() {
        return new Queue(RETURN_QUEUE, true);
    }

    @Bean
    public Queue availableQueue() {
        return new Queue(AVAILABLE_QUEUE, true);
    }
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}