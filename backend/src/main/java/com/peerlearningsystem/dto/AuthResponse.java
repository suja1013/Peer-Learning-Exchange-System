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
}
