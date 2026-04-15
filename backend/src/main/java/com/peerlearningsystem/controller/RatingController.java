package com.peerlearningsystem.controller;

import com.peerlearningsystem.dto.SubmitRating;
import com.peerlearningsystem.model.Rating;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {


    private final RatingService ratingService;
    private final UserRepository userRepository;

    // SUBMIT rating (learner only)
    @PostMapping
    public ResponseEntity<?> submitRating(@RequestBody SubmitRating body, Authentication auth) {
        try {
            User learner = getUser(auth);
            Rating rating = ratingService.submitRating(
                    body.getMeetingId(), learner, body.getRating(), body.getFeedback());
            return ResponseEntity.ok(rating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET all ratings for a tutor
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<Rating>> getTutorRatings(@PathVariable Long tutorId) {
        return ResponseEntity.ok(ratingService.getTutorRatings(tutorId));
    }

    // GET average rating for a tutor
    @GetMapping("/tutor/{tutorId}/avg")
    public ResponseEntity<Double> getTutorAvgRating(@PathVariable Long tutorId) {
        return ResponseEntity.ok(ratingService.getTutorAverageRating(tutorId));
    }

    // CHECK if a rating exists for a meeting
    // Returns 200 with the rating if it exists, 404 if not yet rated.
    // Frontend uses this to hide the "Rate Tutor" button after submission.
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<?> getRatingByMeeting(@PathVariable Long meetingId) {
        return ratingService.getRatingByMeeting(meetingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
