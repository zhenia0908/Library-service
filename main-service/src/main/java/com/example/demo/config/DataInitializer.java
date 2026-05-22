package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class DataInitializer {
        @Bean
        CommandLineRunner fillDatabase(UserRepository userRepository) {
            return args -> {
                String readerEmail = "neville.longbottom@pwr.edu.pl";
                if (userRepository.findByEmail(readerEmail) == null) {
                    User user = new User();
                    user.setName("Neville");
                    user.setEmail(readerEmail);
                    user.setPassword("reader");
                    user.setRole(Role.READER);
                    user.setActive(true);
                    user.setBorrowLimit(5);
                    userRepository.save(user);
                }
                System.out.println("The data base was filled!");
            };
        }
    }

