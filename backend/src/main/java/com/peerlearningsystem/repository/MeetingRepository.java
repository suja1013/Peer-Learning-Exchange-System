package com.peerlearningsystem.repository;

import com.peerlearningsystem.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // One meeting per session request
    Optional<Meeting> findBySessionRequestId(Long sessionRequestId);

    // All meetings where learner is the learner of the session request
    List<Meeting> findBySessionRequestLearnerId(Long learnerId);

    // All meetings where tutor is the tutor of the session request
    List<Meeting> findBySessionRequestTutorId(Long tutorId);
}

