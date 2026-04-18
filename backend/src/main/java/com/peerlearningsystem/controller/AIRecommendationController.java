package com.peerlearningsystem.controller;
import com.peerlearningsystem.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.peerlearningsystem.dto.RankedTutor;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<?> getRecommendations(
            @RequestParam String skill,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<RankedTutor> ranked = recommendationService.getRecommendations(skill, limit);
            return ResponseEntity.ok(ranked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
