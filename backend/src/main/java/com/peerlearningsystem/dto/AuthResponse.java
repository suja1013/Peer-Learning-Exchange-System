package com.peerlearningsystem.dto;

import com.peerlearningsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private User.UserRole role;
    private Integer activationPoints;

    public AuthResponse(String token, User user) {
        this.token            = token;
        this.userId           = user.getId();
        this.username         = user.getUsername();
        this.email            = user.getEmail();
        this.role             = user.getRole();
        this.activationPoints = user.getActivationPoints();
    }
}
