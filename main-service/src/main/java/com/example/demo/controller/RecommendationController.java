package com.example.demo.controller;

import com.example.demo.service.RecommendationClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationClient client;

    public RecommendationController(RecommendationClient client) {
        this.client = client;
    }

    @GetMapping("/{userId}")
    public List<String> getRecommendations(
            @PathVariable Long userId) {

        return client.getRecommendations(userId);
    }
}
