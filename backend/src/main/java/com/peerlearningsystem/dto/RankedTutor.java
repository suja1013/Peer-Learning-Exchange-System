package com.peerlearningsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.peerlearningsystem.model.UserTeachingSkill;

// Ranked result DTO — flat JSON for frontend
public class RankedTutor {

    @JsonIgnore
    private final UserTeachingSkill teachingSkill;

    private final double score;
    private final double avgRating;
    private final int completedSessions;
    private final boolean exactMatch;
    private final String matchedSkill;

    public RankedTutor(UserTeachingSkill ts,
                       double score,
                       double avgRating,
                       int completedSessions,
                       boolean exactMatch,
                       String matchedSkill) {
        this.teachingSkill = ts;
        this.score = Math.round(score * 100.0) / 100.0;
        this.avgRating = Math.round(avgRating * 10.0) / 10.0;
        this.completedSessions = completedSessions;
        this.exactMatch = exactMatch;
        this.matchedSkill = matchedSkill;
    }

    public Long getTutorId() {
        return teachingSkill.getUser().getId();
    }

    public String getTutorFullName() {
        return teachingSkill.getUser().getFullName();
    }

    public String getTutorUsername() {
        return teachingSkill.getUser().getUsername();
    }

    public int getTutorPoints() {
        return teachingSkill.getUser().getActivationPoints();
    }

    public Long getSkillId() {
        return teachingSkill.getSkill().getId();
    }

    public String getSkillName() {
        return teachingSkill.getSkill().getName();
    }

    public String getExperienceLevel() {
        return teachingSkill.getExperienceLevel() != null
                ? teachingSkill.getExperienceLevel().name()
                : "BEGINNER";
    }

    public String getDescription() {
        return teachingSkill.getDescription();
    }

    public double getScore() {
        return score;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public String getMatchedSkill() {
        return matchedSkill;
    }
}
