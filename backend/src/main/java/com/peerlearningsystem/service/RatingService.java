package com.peerlearningsystem.service;

import com.peerlearningsystem.model.*;
import com.peerlearningsystem.repository.MeetingRepository;
import com.peerlearningsystem.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MeetingRepository meetingRepository;

    // SUBMIT a rating
    @Transactional
    public Rating submitRating(Long meetingId, User learner, int stars, String feedback) {

        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        if (!meeting.getSessionRequest().getLearner().getId().equals(learner.getId())) {
            throw new IllegalArgumentException("Only the learner can rate this session");
        }
        if (meeting.getSessionRequest().getStatus() != SessionRequest.RequestStatus.COMPLETED) {
            throw new IllegalArgumentException("You can only rate a completed session");
        }

        ratingRepository.findByMeetingId(meetingId).ifPresent(r -> {
            throw new IllegalArgumentException("You have already rated this session");
        });

        User tutor = meeting.getSessionRequest().getTutor();

        Rating rating = Rating.builder()
                .meeting(meeting)
                .learner(learner)
                .tutor(tutor)
                .rating(stars)
                .feedback(feedback)
                .build();

        return ratingRepository.save(rating);
    }

    // GET all ratings for a tutor
    public List<Rating> getTutorRatings(Long tutorId) {
        return ratingRepository.findByTutorId(tutorId);
    }

    // GET average rating for a tutor
    public Double getTutorAverageRating(Long tutorId) {
        Double avg = ratingRepository.findAverageRatingByTutorId(tutorId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // CHECK if a rating exists for a meeting
    // Returns Optional.empty() if not yet rated — controller returns 404
    public Optional<Rating> getRatingByMeeting(Long meetingId) {
        return ratingRepository.findByMeetingId(meetingId);
    }

}
