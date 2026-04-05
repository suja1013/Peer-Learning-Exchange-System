package com.peerlearningsystem.controller;

import com.peerlearningsystem.dto.SendRequest;
import com.peerlearningsystem.model.SessionRequest;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.service.SessionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class SessionRequestController {

    private final SessionRequestService sessionRequestService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> sendRequest(@RequestBody SendRequest body, Authentication auth) {
        try {
            User learner = getUser(auth);
            SessionRequest req = sessionRequestService.sendRequest(
                    learner, body.getTutorId(), body.getSkillId(), body.getMessage());
            return ResponseEntity.ok(req);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<SessionRequest>> getIncoming(Authentication auth) {
        return ResponseEntity.ok(
                sessionRequestService.getIncomingRequests(getUser(auth).getId()));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<SessionRequest>> getOutgoing(Authentication auth) {
        return ResponseEntity.ok(
                sessionRequestService.getOutgoingRequests(getUser(auth).getId()));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(
                    sessionRequestService.acceptRequest(id, getUser(auth)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(
                    sessionRequestService.rejectRequest(id, getUser(auth)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(
                    sessionRequestService.cancelRequest(id, getUser(auth)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
