import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { meetingsAPI } from '../../services/apiService';
import './Meetings.css';

// Meeting List
export const Meetings = () => {
  const navigate = useNavigate();

  const [meetings, setMeetings] = useState([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    meetingsAPI.getMyMeetings()
      .then((r) => setMeetings(r.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="meetings-loading">Loading meetings...</div>;

  return (
    <div className="meetings-page">
      <button className="meetings-back-btn" onClick={() => navigate('/dashboard')}>← Back</button>
      <h2 className="meetings-title">🎯 My Meetings</h2>

      {meetings.length === 0 ? (
        <div className="meetings-empty">
          No meetings yet. Accept a session request to get started!
        </div>
      ) : (
        <div className="meetings-list">
          {meetings.map((m) => (
            <div key={m.id} className="meeting-card">
              <div className="meeting-card-info">
                <div className="meeting-skill">
                  🎯 {m.sessionRequest?.skill?.name || 'General Session'}
                </div>
                <div className="meeting-parties">
                  <span>👨‍🏫 {m.sessionRequest?.tutor?.fullName}</span>
                  <span className="meeting-arrow">→</span>
                  <span>🎓 {m.sessionRequest?.learner?.fullName}</span>
                </div>
                {m.scheduledAt && (
                  <div className="meeting-time">
                    🕐 {new Date(m.scheduledAt).toLocaleString()}
                  </div>
                )}
                {m.notes && <div className="meeting-notes">{m.notes}</div>}
                <span className={`meeting-status meeting-status-${m.status?.toLowerCase()}`}>
                  {m.status}
                </span>
              </div>
              <a
                href={m.meetingLink}
                target="_blank"
                rel="noopener noreferrer"
                className="meeting-join-btn"
              >
                Join Meeting
              </a>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

// ── Create Meeting Form (tutor) ──────────────────────────────
export const CreateMeeting = () => {
  const { requestId } = useParams();
  const navigate      = useNavigate();

  const [form, setForm]       = useState({ meetingLink: '', notes: '', scheduledAt: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleCreate = async () => {
    if (!form.meetingLink.trim()) {
      setError('Meeting link is required.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await meetingsAPI.create({
        sessionRequestId: Number(requestId),
        meetingLink: form.meetingLink,
        notes: form.notes,
        scheduledAt: form.scheduledAt || null,
      });
      navigate('/requests');
    } catch (err) {
      setError(err.response?.data || 'Failed to create meeting.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="meetings-page">
      <button className="meetings-back-btn" onClick={() => navigate('/requests')}>← Back</button>
      <h2 className="meetings-title">+ Add Meeting Link</h2>

      <div className="create-meeting-card">
        {error && <div className="meetings-error">{error}</div>}

        <label className="meetings-label">Meeting Link *</label>
        <input
          className="meetings-input"
          placeholder="https://meet.google.com/... or https://zoom.us/..."
          value={form.meetingLink}
          onChange={(e) => setForm({ ...form, meetingLink: e.target.value })}
        />

        <label className="meetings-label">Scheduled Date & Time</label>
        <input
          className="meetings-input"
          type="datetime-local"
          value={form.scheduledAt}
          onChange={(e) => setForm({ ...form, scheduledAt: e.target.value })}
        />

        <label className="meetings-label">Notes (optional)</label>
        <textarea
          className="meetings-textarea"
          placeholder="Any preparation notes for the learner..."
          value={form.notes}
          onChange={(e) => setForm({ ...form, notes: e.target.value })}
          rows={3}
        />

        <div className="meetings-actions">
          <button className="meetings-cancel-btn" onClick={() => navigate('/requests')}>
            Cancel
          </button>
          <button className="meetings-save-btn" onClick={handleCreate} disabled={loading}>
            {loading ? 'Saving...' : 'Send Meeting Link'}
          </button>
        </div>
      </div>
    </div>
  );
};
