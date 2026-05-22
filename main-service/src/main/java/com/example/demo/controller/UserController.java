package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {
    private final UserService service;
    private final UserRepository userRepository;

    public UserController(UserService service, UserRepository userRepository) {

        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getAll() {
            return service.getAll();
        }

    @PostMapping("/user/create")
    public User create(@RequestBody User user) {
        return service.save(user);
    }

    @GetMapping("/api/user/info")
    public Map<String, String> getUserInfo(Authentication authentication) {
            String role = authentication.getAuthorities().stream()
                    .findFirst().get().getAuthority();
            String email = authentication.getName();
            Long userId = userRepository.findByEmail(email).getId();
            return Map.of("id", String.valueOf(userId),"role", role.replace("ROLE_", ""), "email", email);
        }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User existingUser = service.findById(id);
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setName(userDetails.getName());

        if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        if (userDetails.getBorrowLimit() != null) {
            existingUser.setBorrowLimit(userDetails.getBorrowLimit());
        }

        User updatedUser = service.save(existingUser);

        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
    @PutMapping("/users/{id}/status")
    public User updateStatus(@PathVariable Long id,
                             @RequestBody Map<String, Boolean> body) {

        User user = userRepository.findById(id)
                .orElseThrow();

        user.setActive(body.get("active"));

        return userRepository.save(user);
    }
    }



