package com.peerlearningsystem.service;

import com.peerlearningsystem.model.User;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.points.registration-bonus}")
    private Integer registrationBonus;

    public User register(String username, String email, String password,
                         String fullName, User.UserRole role) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role != null ? role : User.UserRole.BOTH)
                .activationPoints(registrationBonus)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        return user;
    }

    public String generateToken(String email) {
        return jwtUtils.generateToken(email);
    }
}
