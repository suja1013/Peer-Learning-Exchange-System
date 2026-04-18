package com.peerlearningsystem.service;

import com.peerlearningsystem.dto.RankedTutor;
import com.peerlearningsystem.model.SessionRequest;
import com.peerlearningsystem.model.UserTeachingSkill;
import com.peerlearningsystem.repository.RatingRepository;
import com.peerlearningsystem.repository.SessionRequestRepository;
import com.peerlearningsystem.repository.TutorSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIRecommendationService {

    private final TutorSearchRepository tutorSearchRepository;
    private final RatingRepository ratingRepository;
    private final SessionRequestRepository sessionRequestRepository;

    // Related skills map for intelligent matching
    private static final Map<String, List<String>> RELATED_SKILLS = new HashMap<>();

    static {
        RELATED_SKILLS.put("react", Arrays.asList("javascript", "html", "css", "typescript", "next.js"));
        RELATED_SKILLS.put("angular", Arrays.asList("javascript", "typescript", "html", "css"));
        RELATED_SKILLS.put("vue", Arrays.asList("javascript", "html", "css", "nuxt"));
        RELATED_SKILLS.put("javascript", Arrays.asList("typescript", "react", "node.js", "html", "css"));
        RELATED_SKILLS.put("typescript", Arrays.asList("javascript", "react", "angular", "node.js"));
        RELATED_SKILLS.put("node.js", Arrays.asList("javascript", "express", "mongodb", "rest api"));
        RELATED_SKILLS.put("python", Arrays.asList("django", "flask", "machine learning", "data science", "pandas"));
        RELATED_SKILLS.put("machine learning", Arrays.asList("python", "deep learning", "tensorflow", "data science"));
        RELATED_SKILLS.put("java", Arrays.asList("spring boot", "kotlin", "maven", "microservices"));
        RELATED_SKILLS.put("spring boot", Arrays.asList("java", "microservices", "rest api", "hibernate"));
        RELATED_SKILLS.put("sql", Arrays.asList("mysql", "postgresql", "database design", "mongodb"));
        RELATED_SKILLS.put("mysql", Arrays.asList("sql", "postgresql", "database design"));
        RELATED_SKILLS.put("docker", Arrays.asList("kubernetes", "devops", "ci/cd", "linux"));
        RELATED_SKILLS.put("kubernetes", Arrays.asList("docker", "devops", "cloud", "microservices"));
        RELATED_SKILLS.put("aws", Arrays.asList("cloud", "docker", "devops", "azure", "gcp"));
        RELATED_SKILLS.put("html", Arrays.asList("css", "javascript", "react", "web design"));
        RELATED_SKILLS.put("css", Arrays.asList("html", "javascript", "react", "tailwind"));
        RELATED_SKILLS.put("data science", Arrays.asList("python", "machine learning", "statistics", "pandas"));
        RELATED_SKILLS.put("kotlin", Arrays.asList("java", "android", "spring boot"));
        RELATED_SKILLS.put("flutter", Arrays.asList("dart", "mobile development", "ios", "android"));
        RELATED_SKILLS.put("testing", Arrays.asList("junit", "selenium", "cypress", "qa", "software testing"));
        RELATED_SKILLS.put("git", Arrays.asList("github", "gitlab", "devops", "version control"));
    }

    // Main: return AI-ranked tutors with limit
    public List<RankedTutor> getRecommendations(String skillQuery, int limit) {
        if (skillQuery == null || skillQuery.isBlank()) {
            throw new IllegalArgumentException("Skill name must not be blank");
        }

        String query = skillQuery.trim().toLowerCase();

        // 1. Exact matches
        List<UserTeachingSkill> exactMatches = tutorSearchRepository.findTutorsBySkillName(query);

        // 2. Related matches — remove exact duplicates
        Set<Long> exactIds = exactMatches.stream()
                .map(UserTeachingSkill::getId)
                .collect(Collectors.toSet());

        List<UserTeachingSkill> relatedMatches = RELATED_SKILLS
                .getOrDefault(query, Collections.emptyList())
                .stream()
                .flatMap(relatedSkill -> tutorSearchRepository.findTutorsBySkillName(relatedSkill).stream())
                .filter(ts -> !exactIds.contains(ts.getId()))
                .collect(Collectors.toList());

        // 3. Score and combine
        List<RankedTutor> ranked = new ArrayList<>();

        exactMatches.forEach(ts -> ranked.add(score(ts, true)));
        relatedMatches.forEach(ts -> ranked.add(score(ts, false)));

        // 4. Sort: exact matches first, then by score descending
        ranked.sort(
                Comparator.comparing(RankedTutor::isExactMatch).reversed()
                        .thenComparing(RankedTutor::getScore, Comparator.reverseOrder())
        );

        // 5. Apply limit
        int cap = (limit > 0) ? limit : 10;
        return ranked.stream().limit(cap).collect(Collectors.toList());
    }

    // Score a single tutor-skill entry
    private RankedTutor score(UserTeachingSkill ts, boolean exactMatch) {
        Long tutorId = ts.getUser().getId();

        // 40% — avg rating (0–5 normalized to 0–1)
        Double rawAvg = ratingRepository.findAverageRatingByTutorId(tutorId);
        double avgRating = rawAvg != null ? rawAvg : 0.0;
        double ratingScore = avgRating / 5.0;

        // 30% — completed sessions (capped at 20)
        int completed = sessionRequestRepository
                .findByTutorIdAndStatus(tutorId, SessionRequest.RequestStatus.COMPLETED)
                .size();
        double sessionScore = Math.min(completed / 20.0, 1.0);

        // 20% — experience level
        double expScore = switch (ts.getExperienceLevel()) {
            case EXPERT -> 1.0;
            case INTERMEDIATE -> 0.6;
            default -> 0.3;
        };

        // 10% — relevance
        double relevanceScore = exactMatch ? 1.0 : 0.4;

        double finalScore =
                (ratingScore * 0.40) + (sessionScore * 0.30) + (expScore * 0.20) + (relevanceScore * 0.10);

        return new RankedTutor(
                ts,
                finalScore,
                avgRating,
                completed,
                exactMatch,
                ts.getSkill().getName()
        );
    }
}