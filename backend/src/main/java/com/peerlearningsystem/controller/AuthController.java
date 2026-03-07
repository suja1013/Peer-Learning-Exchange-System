package com.peerlearningsystem.controller;

import com.peerlearningsystem.dto.AuthResponse;
import com.peerlearningsystem.dto.LoginRequest;
import com.peerlearningsystem.dto.RegisterRequest;
import com.peerlearningsystem.exception.CustomException;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.security.JwtUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    public UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.points.registration-bonus}")
    private Integer registrationBonus;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new CustomException("Email already in use"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole() : User.UserRole.BOTH)
                .activationPoints(registrationBonus)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, saved.getId(), saved.getUsername(),
                saved.getEmail(), saved.getRole(), saved.getActivationPoints()));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(new CustomException("Invalid email or password"));
        }

        if (!user.getIsActive()) {
            return ResponseEntity.badRequest().body(new CustomException("Account is deactivated"));
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getUsername(),
                user.getEmail(), user.getRole(), user.getActivationPoints()));
    }


}
