package com.peerlearningsystem.dto;

import com.peerlearningsystem.model.UserTeachingSkill;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddSkillSetRequest {

    @NotBlank(message = "Skill name must not be blank")
    private String skillName;
    private String experienceLevel;
    private String description;
}
