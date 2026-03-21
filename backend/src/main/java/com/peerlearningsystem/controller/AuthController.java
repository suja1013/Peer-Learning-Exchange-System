package com.peerlearningsystem.controller;

import com.peerlearningsystem.dto.AuthResponse;
import com.peerlearningsystem.dto.ErrorResponse;
import com.peerlearningsystem.dto.LoginRequest;
import com.peerlearningsystem.dto.RegisterRequest;

import com.peerlearningsystem.model.User;

import com.peerlearningsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                    request.getUsername(), request.getEmail(),
                    request.getPassword(), request.getFullName(), request.getRole()
            );
            String token = authService.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());
            String token = authService.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        }
    }
}
