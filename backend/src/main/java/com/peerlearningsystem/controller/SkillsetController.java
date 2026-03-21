package com.peerlearningsystem.controller;


import com.peerlearningsystem.dto.AddSkillSetRequest;
import com.peerlearningsystem.model.Skill;
import com.peerlearningsystem.model.User;
import com.peerlearningsystem.model.UserLearningSkill;
import com.peerlearningsystem.model.UserTeachingSkill;
import com.peerlearningsystem.repository.UserRepository;
import com.peerlearningsystem.service.SkillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillsetController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    // GET /api/skills
    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    // GET /api/skills/search?name=java
    @GetMapping("/search")
    public ResponseEntity<List<Skill>> searchSkills(@RequestParam String name) {
        return ResponseEntity.ok(skillService.searchSkillsByName(name));
    }

    // GET /api/skills/my/teaching
    @GetMapping("/my/teaching")
    public ResponseEntity<List<UserTeachingSkill>> getMyTeachingSkills(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(skillService.getTeachingSkills(user.getId()));
    }

    // GET /api/skills/my/learning
    @GetMapping("/my/learning")
    public ResponseEntity<List<UserLearningSkill>> getMyLearningSkills(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(skillService.getLearningSkills(user.getId()));
    }

    // POST /api/skills/my/teaching
    @PostMapping("/my/teaching")
    public ResponseEntity<?> addTeachingSkill(@Valid @RequestBody AddSkillSetRequest request, Authentication auth) {
        try {
            User user = getUser(auth);
            UserTeachingSkill saved = skillService.addTeachingSkillByName(
                    user,
                    request.getSkillName(),
                    request.getExperienceLevel(),
                    request.getDescription()
            );
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/skills/my/learning
    @PostMapping("/my/learning")
    public ResponseEntity<?> addLearningSkill(@Valid @RequestBody AddSkillSetRequest request, Authentication auth) {
        try {
            User user = getUser(auth);
            UserLearningSkill saved = skillService.addLearningSkillByName(
                    user,
                    request.getSkillName()
            );
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/skills/my/teaching/{skillId}
    @DeleteMapping("/my/teaching/{skillId}")
    public ResponseEntity<Void> removeTeachingSkill(@PathVariable Long skillId, Authentication auth) {
        skillService.removeTeachingSkill(getUser(auth).getId(), skillId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/skills/my/learning/{skillId}
    @DeleteMapping("/my/learning/{skillId}")
    public ResponseEntity<Void> removeLearningSkill(@PathVariable Long skillId, Authentication auth) {
        skillService.removeLearningSkill(getUser(auth).getId(), skillId);
        return ResponseEntity.noContent().build();
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
