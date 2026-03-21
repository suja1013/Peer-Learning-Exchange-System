package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.UserTeachingSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeachingSkillRepository extends JpaRepository<UserTeachingSkill, Long> {

    List<UserTeachingSkill> findByUserId(Long userId);
    void deleteByUserIdAndSkillId(Long userId, Long skillId);

    UserTeachingSkill save(UserTeachingSkill teaching);
}
