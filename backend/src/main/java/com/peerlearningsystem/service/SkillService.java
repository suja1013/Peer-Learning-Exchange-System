package com.peerlearningsystem.service;

import com.peerlearningsystem.CustomException;
import com.peerlearningsystem.model.Skill;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.model.UserLearningSkill;
import com.peerlearningsystem.model.UserTeachingSkill;
import com.peerlearningsystem.repository.SkillRepository;
import com.peerlearningsystem.repository.UserLearningSkillRepository;
import com.peerlearningsystem.repository.UserTeachingSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SkillService {


    private final SkillRepository skillRepository;
    private final UserTeachingSkillRepository teachingSkillRepo;
    private final UserLearningSkillRepository learningSkillRepo;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public List<Skill> searchSkillsByName(String name) {
        return skillRepository.findByNameContainingIgnoreCase(name);
    }

    public List<UserTeachingSkill> getTeachingSkills(Long userId) {
        return teachingSkillRepo.findByUserId(userId);
    }

    public List<UserLearningSkill> getLearningSkills(Long userId) {
        return learningSkillRepo.findByUserId(userId);
    }

    // ── Find or create skill by name, then add as teaching skill ──
    @Transactional
    public UserTeachingSkill addTeachingSkillByName(User user, String skillName,
                                                    String experienceLevelStr,
                                                    String description) {
        // If skill already exists reuse it, otherwise create a new one
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                                .name(capitalize(skillName))
                                .category("General")
                                .build()
                ));

        // Parse experience level safely, default to INTERMEDIATE
        UserTeachingSkill.ExperienceLevel level;
        try {
            level = (experienceLevelStr != null && !experienceLevelStr.isBlank())
                    ? UserTeachingSkill.ExperienceLevel.valueOf(experienceLevelStr.toUpperCase())
                    : UserTeachingSkill.ExperienceLevel.INTERMEDIATE;
        } catch (IllegalArgumentException e) {
            level = UserTeachingSkill.ExperienceLevel.INTERMEDIATE;
        }

        UserTeachingSkill teaching = UserTeachingSkill.builder()
                .user(user)
                .skill(skill)
                .experienceLevel(level)
                .description(description)
                .build();

        return teachingSkillRepo.save(teaching);
    }

    // ── Find or create skill by name, then add as learning skill ──
    @Transactional
    public UserLearningSkill addLearningSkillByName(User user, String skillName) {
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                                .name(capitalize(skillName))
                                .category("General")
                                .build()
                ));

        UserLearningSkill learning = UserLearningSkill.builder()
                .user(user)
                .skill(skill)
                .build();

        return learningSkillRepo.save(learning);
    }

    @Transactional
    public void removeTeachingSkill(Long userId, Long skillId) {
        teachingSkillRepo.deleteByUserIdAndSkillId(userId, skillId);
    }

    @Transactional
    public void removeLearningSkill(Long userId, Long skillId) {
        learningSkillRepo.deleteByUserIdAndSkillId(userId, skillId);
    }


    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1); // ← remove .toLowerCase()
    }
}
