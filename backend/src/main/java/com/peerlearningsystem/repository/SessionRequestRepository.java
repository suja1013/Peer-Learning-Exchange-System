package com.peerlearningsystem.repository;

import org.springframework.stereotype.Repository;
import com.peerlearningsystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface SessionRequestRepository extends JpaRepository<SessionRequest, Long>{

    // All requests sent BY a learner
    List<SessionRequest> findByLearnerId(Long learnerId);

    // All requests received BY a tutor
    List<SessionRequest> findByTutorId(Long tutorId);

    // Requests by tutor + status filter
    List<SessionRequest> findByTutorIdAndStatus(Long tutorId, SessionRequest.RequestStatus status);

    // Requests by learner + status filter
    List<SessionRequest> findByLearnerIdAndStatus(Long learnerId, SessionRequest.RequestStatus status);
}
