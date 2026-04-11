package com.peerlearningsystem.controller;
import com.peerlearningsystem.dto.CreateMeeting;
import com.peerlearningsystem.model.Meeting;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final UserRepository userRepository;

    //CREATE meeting (tutor provides link after accepting)
    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody CreateMeeting body, Authentication auth) {
        try {
            User tutor = getUser(auth);
            LocalDateTime scheduledAt = body.getScheduledAt() != null
                    ? LocalDateTime.parse(body.getScheduledAt()) : null;

            Meeting meeting = meetingService.createMeeting(
                    body.getSessionRequestId(), tutor,
                    body.getMeetingLink(), body.getNotes(), scheduledAt);

            return ResponseEntity.ok(meeting);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET all my meetings (as tutor or learner)
    @GetMapping
    public ResponseEntity<List<Meeting>> getMyMeetings(Authentication auth) {
        return ResponseEntity.ok(meetingService.getMyMeetings(getUser(auth)));
    }

    //GET single meeting by meeting ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(meetingService.getMeeting(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //GET meeting by session request ID
    @GetMapping("/request/{requestId}")
    public ResponseEntity<?> getMeetingByRequest(@PathVariable Long requestId) {
        try {
            return ResponseEntity.ok(meetingService.getMeetingByRequestId(requestId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

