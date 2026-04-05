package com.peerlearningsystem.service;

import com.peerlearningsystem.model.*;
import com.peerlearningsystem.repository.SessionRequestRepository;
import com.peerlearningsystem.repository.SkillRepository;
import com.peerlearningsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SessionRequestService {

    private final SessionRequestRepository sessionRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Value("${app.points.session-cost}")
    private Integer sessionCost;


    @Transactional
    public SessionRequest sendRequest(User learner, Long tutorId, Long skillId, String message) {

        // Validate tutor exists
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Tutor not found"));

        if (tutor.getId().equals(learner.getId())) {
            throw new IllegalArgumentException("You cannot send a request to yourself");
        }

        // Validate learner has enough points
        if (learner.getActivationPoints() < sessionCost) {
            throw new IllegalArgumentException(
                    "Not enough activation points. You need " + sessionCost +
                            " points but have " + learner.getActivationPoints());
        }

        // Resolve optional skill
        Skill skill = null;
        if (skillId != null) {
            skill = skillRepository.findById(skillId).orElse(null);
        }

        // Deduct points from learner immediately on sending
        learner.setActivationPoints(learner.getActivationPoints() - sessionCost);
        userRepository.save(learner);

        SessionRequest request = SessionRequest.builder()
                .learner(learner)
                .tutor(tutor)
                .skill(skill)
                .message(message)
                .status(SessionRequest.RequestStatus.PENDING)
                .pointsDeducted(sessionCost)
                .build();

        return sessionRequestRepository.save(request);
    }

    public List<SessionRequest> getIncomingRequests(Long tutorId) {
        return sessionRequestRepository.findByTutorId(tutorId);
    }


    public List<SessionRequest> getOutgoingRequests(Long learnerId) {
        return sessionRequestRepository.findByLearnerId(learnerId);
    }

    @Transactional
    public SessionRequest acceptRequest(Long requestId, User tutor) {
        SessionRequest request = getRequestOrThrow(requestId);
        validateTutorOwns(request, tutor);
        validatePending(request);

        request.setStatus(SessionRequest.RequestStatus.ACCEPTED);
        return sessionRequestRepository.save(request);
    }

    @Transactional
    public SessionRequest rejectRequest(Long requestId, User tutor) {
        SessionRequest request = getRequestOrThrow(requestId);
        validateTutorOwns(request, tutor);
        validatePending(request);

        // Refund points to learner on rejection
        User learner = request.getLearner();
        learner.setActivationPoints(learner.getActivationPoints() + request.getPointsDeducted());
        userRepository.save(learner);

        request.setStatus(SessionRequest.RequestStatus.REJECTED);
        return sessionRequestRepository.save(request);
    }

    @Transactional
    public SessionRequest cancelRequest(Long requestId, User learner) {
        SessionRequest request = getRequestOrThrow(requestId);

        if (!request.getLearner().getId().equals(learner.getId())) {
            throw new IllegalArgumentException("Only the learner can cancel this request");
        }
        validatePending(request);

        // Refund points on cancel
        learner.setActivationPoints(learner.getActivationPoints() + request.getPointsDeducted());
        userRepository.save(learner);

        request.setStatus(SessionRequest.RequestStatus.CANCELLED);
        return sessionRequestRepository.save(request);
    }

    public SessionRequest getRequestOrThrow(Long requestId) {
        return sessionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Session request not found"));
    }

    private void validateTutorOwns(SessionRequest request, User tutor) {
        if (!request.getTutor().getId().equals(tutor.getId())) {
            throw new IllegalArgumentException("You are not the tutor for this request");
        }
    }

    private void validatePending(SessionRequest request) {
        if (request.getStatus() != SessionRequest.RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Request is already " + request.getStatus() + " and cannot be changed");
        }
    }
}
