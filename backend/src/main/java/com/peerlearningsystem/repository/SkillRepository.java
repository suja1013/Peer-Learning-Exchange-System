package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByNameContainingIgnoreCase(String name);
    java.util.Optional<Skill> findByNameIgnoreCase(String name);
    List<Skill> findAll();
}


