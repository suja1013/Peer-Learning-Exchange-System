package com.peerlearningsystem.service;

import com.peerlearningsystem.model.UserTeachingSkill;
import com.peerlearningsystem.repository.TutorSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorSearchService {


    private final TutorSearchRepository tutorSearchRepository;

    // Search tutors who teach a specific skill by skill ID
    public List<UserTeachingSkill> searchBySkillId(Long skillId) {
        return tutorSearchRepository.findTutorsBySkillId(skillId);
    }

    // Search tutors who teach a skill matching the name keyword
    public List<UserTeachingSkill> searchBySkillName(String skillName) {
        if (skillName == null || skillName.isBlank()) {
            throw new IllegalArgumentException("Skill name must not be blank");
        }
        return tutorSearchRepository.findTutorsBySkillName(skillName.trim());
    }
}
