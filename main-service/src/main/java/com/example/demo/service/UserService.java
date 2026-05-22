package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Role;
import com.example.demo.model.Status;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.config.SecureConfig;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo){this.repo = repo;}

    public List<User> getAll() {
        return repo.findAll();
    }

    public User save(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.READER);
        }
        if (user.getActive() == null) {
            user.setActive(true);
        }

        return repo.save(user);
    }
    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
