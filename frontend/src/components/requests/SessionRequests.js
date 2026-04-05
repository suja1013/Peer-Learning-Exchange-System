import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { requestsAPI } from '../../services/apiService';
import './Requests.css';

export const SessionRequests = () => {
  const navigate = useNavigate();

  const [tab, setTab] = useState('incoming');
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
     setLoading(true);
  try {
    const [inc, out] = await Promise.all([
      requestsAPI.getIncoming(),
      requestsAPI.getOutgoing(),
    ]);

    // Hide cancelled requests from tutor incoming list
    const filteredIncoming = inc.data.filter(
      (req) => req.status !== 'CANCELLED'
    );

    setIncoming(filteredIncoming);
    setOutgoing(out.data);
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

  const RequestCard = ({ req, isIncoming }) => (
    <div className="req-card">
      <div className="req-card-header">
        <div>
          <div className="req-card-name">
            {isIncoming ? req.learner?.fullName : req.tutor?.fullName}
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

        {isIncoming && req.status === 'ACCEPTED' && (
          <button
            className="req-meeting-btn"
            onClick={() => navigate(`/meetings/create/${req.id}`)}
          >
            + Add Meeting Link
          </button>
        )}

        {!isIncoming && req.status === 'PENDING' && (
          <button
            className="req-cancel-action-btn"
            onClick={() => doAction(() => requestsAPI.cancel(req.id))}
          >
            Cancel Request
          </button>
        )}
      </div>
    </div>
  );

  if (loading) {
    return <div className="requests-loading">Loading requests...</div>;
  }

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

      {tab === 'outgoing' && (
        <div className="req-search-top">
          <button className="req-find-btn" onClick={() => navigate('/search')}>
            Find Tutors
          </button>
        </div>
      )}

      <div className="req-list">
        {list.length === 0 ? (
          <div className="req-empty">No {tab} requests yet.</div>
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