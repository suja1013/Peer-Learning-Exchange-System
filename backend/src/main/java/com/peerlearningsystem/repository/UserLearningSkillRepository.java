package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.UserLearningSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLearningSkillRepository extends JpaRepository<UserLearningSkill, Long> {

    List<UserLearningSkill> findByUserId(Long userId);
    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    UserLearningSkill save(UserLearningSkill learning);
}
