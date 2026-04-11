package com.peerlearningsystem.service;

import com.peerlearningsystem.model.*;
import com.peerlearningsystem.repository.MeetingRepository;
import com.peerlearningsystem.repository.SessionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final SessionRequestRepository sessionRequestRepository;

    //CREATE meeting (tutor only, request must be ACCEPTED)
    @Transactional
    public Meeting createMeeting(Long requestId, User tutor,
                                 String meetingLink, String notes,
                                 LocalDateTime scheduledAt) {

        SessionRequest request = sessionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Session request not found"));

        if (!request.getTutor().getId().equals(tutor.getId())) {
            throw new IllegalArgumentException("Only the tutor can create a meeting for this request");
        }
        if (request.getStatus() != SessionRequest.RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Request must be ACCEPTED before creating a meeting");
        }

        // Prevent duplicate meeting for same request
        meetingRepository.findBySessionRequestId(requestId).ifPresent(m -> {
            throw new IllegalArgumentException("A meeting already exists for this request");
        });

        Meeting meeting = Meeting.builder()
                .sessionRequest(request)
                .meetingLink(meetingLink)
                .notes(notes)
                .scheduledAt(scheduledAt)
                .status(Meeting.MeetingStatus.SCHEDULED)
                .build();

        return meetingRepository.save(meeting);
    }

    // GET all meetings for logged-in user (as learner or tutor)
    public List<Meeting> getMyMeetings(User user) {
        List<Meeting> asTutor   = meetingRepository.findBySessionRequestTutorId(user.getId());
        List<Meeting> asLearner = meetingRepository.findBySessionRequestLearnerId(user.getId());
        asTutor.addAll(asLearner);
        return asTutor;
    }

    // GET single meeting by ID
    public Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
    }

    //GET meeting by session request ID
    public Meeting getMeetingByRequestId(Long requestId) {
        return meetingRepository.findBySessionRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("No meeting found for this request"));
    }
}

