package com.peerlearningsystem.controller;

import com.peerlearningsystem.model.UserTeachingSkill;
import com.peerlearningsystem.service.TutorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorSearchController {

    private final TutorSearchService tutorSearchService;

    @GetMapping("/search")
    public ResponseEntity<?> searchTutors(
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) String skillName) {

        try {
            List<UserTeachingSkill> results;

            if (skillId != null) {
                results = tutorSearchService.searchBySkillId(skillId);
            } else if (skillName != null && !skillName.isBlank()) {
                results = tutorSearchService.searchBySkillName(skillName);
            } else {
                return ResponseEntity.badRequest()
                        .body("Provide either skillId or skillName as query parameter");
            }

            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
