package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // All ratings for a tutor
    List<Rating> findByTutorId(Long tutorId);

    // Check if rating already exists for this meeting
    Optional<Rating> findByMeetingId(Long meetingId);

    // Average rating for a tutor
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.tutor.id = :tutorId")
    Double findAverageRatingByTutorId(@Param("tutorId") Long tutorId);

}
