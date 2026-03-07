package com.peerlearningsystem.dto;

import com.peerlearningsystem.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;
    @Email
    @NotBlank private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String fullName;
    private User.UserRole role;
}
