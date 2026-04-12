
// src/components/requests/SessionRequests.js
// F4: Manage incoming/outgoing session requests
// F6: Learner confirms session completion → transfers points

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { requestsAPI, meetingsAPI } from '../../services/apiService';
import './Requests.css';

export const SessionRequests = () => {
  const navigate = useNavigate();

  const [tab, setTab] = useState('incoming');
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState('');
  const [meetingMap, setMeetingMap] = useState({});

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [inc, out, myMeetings] = await Promise.all([
        requestsAPI.getIncoming(),
        requestsAPI.getOutgoing(),
        meetingsAPI.getMyMeetings(),
      ]);

      const filteredIncoming = (inc.data || []).filter(
        (req) => req.status !== 'CANCELLED'
      );

      setIncoming(filteredIncoming);
      setOutgoing(out.data || []);

      const map = {};
      (myMeetings.data || []).forEach((m) => {
        if (m.sessionRequest?.id) {
          map[m.sessionRequest.id] = m;
        }
      });
      setMeetingMap(map);
    } catch (err) {
      setActionError('Failed to load requests.');
    } finally {
      setLoading(false);
    }
  };

  const doAction = async (apiFn) => {
    setActionError('');
    try {
      await apiFn();
      await loadData();
    } catch (err) {
      setActionError(err.response?.data || 'Action failed. Please try again.');
    }
  };

  const statusColor = {
    PENDING: '#ed8936',
    ACCEPTED: '#48bb78',
    REJECTED: '#fc8181',
    CANCELLED: '#a0aec0',
    COMPLETED: '#4c51bf',
  };

  const displayName = (user) => user?.fullName || user?.email || 'Unknown';

  const canComplete = (meeting) => {
    if (!meeting) return false;
    if (!meeting.scheduledAt) return true;
    return new Date() >= new Date(meeting.scheduledAt);
  };

  const RequestCard = ({ req, isIncoming }) => {
    const meeting = meetingMap[req.id] || null;
    const hasMeeting = !!meeting;
    const allowComplete = canComplete(meeting);

    return (
      <div className="req-card">
        <div className="req-card-header">
          <div style={{ flex: 1 }}>
            <div className="req-card-name">
              {isIncoming ? displayName(req.learner) : displayName(req.tutor)}
              <span className="req-card-username">
                @{isIncoming ? req.learner?.username : req.tutor?.username}
              </span>
            </div>

            {req.skill && (
              <div className="req-card-skill">🎯 {req.skill.name}</div>
            )}

            {req.message && (
              <div className="req-card-message">"{req.message}"</div>
            )}

            {hasMeeting && (
              <div className="req-meeting-info">
                <span className="req-meeting-label">🔗 Meeting link:</span>
                <a
                  href={meeting.meetingLink}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="req-meeting-link"
                >
                  {meeting.meetingLink}
                </a>
                {meeting.scheduledAt && (
                  <div className="req-meeting-time">
                    🕐 Scheduled: {new Date(meeting.scheduledAt).toLocaleString()}
                  </div>
                )}
              </div>
            )}
          </div>

          <span
            className="req-status"
            style={{
              background: `${statusColor[req.status]}22`,
              color: statusColor[req.status],
            }}
          >
            {req.status}
          </span>
        </div>

        <div className="req-card-actions">
          {isIncoming && req.status === 'PENDING' && (
            <>
              <button
                className="req-accept-btn"
                onClick={() => doAction(() => requestsAPI.accept(req.id))}
              >
                ✓ Accept
              </button>
              <button
                className="req-reject-btn"
                onClick={() => doAction(() => requestsAPI.reject(req.id))}
              >
                ✗ Reject
              </button>
            </>
          )}

          {isIncoming && req.status === 'ACCEPTED' && !hasMeeting && (
            <button
              className="req-meeting-btn"
              onClick={() => navigate(`/meetings/create/${req.id}`)}
            >
              + Add Meeting Link
            </button>
          )}

          {isIncoming && req.status === 'ACCEPTED' && hasMeeting && (
            <span className="req-meeting-sent">
              ✅ Meeting link sent to learner
            </span>
          )}

          {!isIncoming && req.status === 'PENDING' && (
            <button
              className="req-cancel-action-btn"
              onClick={() => doAction(() => requestsAPI.cancel(req.id))}
            >
              Cancel Request
            </button>
          )}

          {!isIncoming && req.status === 'ACCEPTED' && !hasMeeting && (
            <span className="req-waiting">
              ⏳ Waiting for tutor to send meeting link...
            </span>
          )}

          {!isIncoming && req.status === 'ACCEPTED' && hasMeeting && (
            <>
              <a
                href={meeting.meetingLink}
                target="_blank"
                rel="noopener noreferrer"
                className="req-join-btn"
              >
                🔗 Join Meeting
              </a>

              {allowComplete ? (
                <button
                  className="req-complete-btn"
                  onClick={() => doAction(() => requestsAPI.complete(req.id))}
                >
                  ✓ Confirm Session Completed
                </button>
              ) : (
                <div className="req-complete-locked">
                  <button className="req-complete-btn disabled" disabled>
                    ✓ Confirm Session Completed
                  </button>
                  <span className="req-complete-hint">
                    🔒 Available after{' '}
                    {new Date(meeting.scheduledAt).toLocaleString()}
                  </span>
                </div>
              )}
            </>
          )}

          {/* {!isIncoming && req.status === 'COMPLETED' && hasMeeting && (
            <button
              className="req-rate-btn"
              onClick={() =>
                navigate(`/ratings/submit?meetingId=${meeting.id}`)
              }
            >
              ⭐ Rate Tutor
            </button>
          )} */}
        </div>
      </div>
    );
  };

  if (loading) return <div className="requests-loading">Loading requests...</div>;

  const list = tab === 'incoming' ? incoming : outgoing;

  return (
    <div className="requests-page">
      <button className="req-back-btn" onClick={() => navigate('/dashboard')}>
        ← Back
      </button>

      <h2 className="requests-title">📨 Session Requests</h2>

      {actionError && <div className="req-error">{actionError}</div>}

      <div className="req-tabs">
        {[
          { key: 'incoming', label: `📥 Incoming (${incoming.length})` },
          { key: 'outgoing', label: `📤 Outgoing (${outgoing.length})` },
        ].map(({ key, label }) => (
          <button
            key={key}
            className={`req-tab${tab === key ? ' active' : ''}`}
            onClick={() => setTab(key)}
          >
            {label}
          </button>
        ))}
      </div>

      <div className="req-list">
        {list.length === 0 ? (
          <div className="req-empty">
            No {tab} requests yet.
            {tab === 'outgoing' && (
              <button
                className="req-find-btn"
                onClick={() => navigate('/search')}
              >
                Find Tutors
              </button>
            )}
          </div>
        ) : (
          list.map((req) => (
            <RequestCard
              key={req.id}
              req={req}
              isIncoming={tab === 'incoming'}
            />
          ))
        )}
      </div>
    </div>
  );
};