package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorSearchRepository extends JpaRepository<UserTeachingSkill, Long> {

    // Find tutors teaching a specific skill by ID
    @Query("SELECT uts FROM UserTeachingSkill uts " +
            "JOIN FETCH uts.user u " +
            "JOIN FETCH uts.skill s " +
            "WHERE s.id = :skillId AND u.isActive = true")
    List<UserTeachingSkill> findTutorsBySkillId(@Param("skillId") Long skillId);

    // Find tutors teaching a skill matching name (partial, case-insensitive)
    @Query("SELECT uts FROM UserTeachingSkill uts " +
            "JOIN FETCH uts.user u " +
            "JOIN FETCH uts.skill s " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skillName, '%')) AND u.isActive = true")
    List<UserTeachingSkill> findTutorsBySkillName(@Param("skillName") String skillName);
}
