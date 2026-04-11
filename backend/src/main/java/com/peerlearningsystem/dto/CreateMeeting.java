package com.peerlearningsystem.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMeeting {

    @NotNull  private Long sessionRequestId;
    @NotBlank private String meetingLink;
    private String notes;
    private String scheduledAt; // ISO-8601: "2026-03-15T14:00:00"
}
