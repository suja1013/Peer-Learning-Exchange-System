package com.peerlearningsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendRequest {

    @NotNull
    private Long tutorId;
    private Long skillId;
    private String message;
}
