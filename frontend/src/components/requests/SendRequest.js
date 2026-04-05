
import React, { useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { requestsAPI } from '../../services/apiService';
import './Requests.css';

export const SendRequest = () => {
  const { tutorId } = useParams();
  const [searchParams] = useSearchParams();
  const skillId   = searchParams.get('skillId');
  const skillName = searchParams.get('skillName');

  const { user }  = useAuth();
  const navigate  = useNavigate();

  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleSend = async () => {
    setLoading(true);
    setError('');
    try {
      await requestsAPI.send({
        tutorId: Number(tutorId),
        skillId: skillId ? Number(skillId) : null,
        message,
      });
      navigate('/requests');
    } catch (err) {
      setError(err.response?.data || 'Failed to send request. You may not have enough points.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="requests-page">
      <button className="req-back-btn" onClick={() => navigate(-1)}>← Back</button>

      <h2 className="requests-title">📨 Send Session Request</h2>

      <div className="send-request-card">
        <div className="send-request-info">
          <p>
            You are requesting a session for <strong>{skillName || 'a skill'}</strong>.
          </p>
          <p className="send-request-cost">
            ⚡ This will cost <strong>20 activation points</strong>. You currently have{' '}
            <strong>{user?.activationPoints} points</strong>.
          </p>
        </div>

        {error && <div className="req-error">{error}</div>}

        <label className="req-label">Message to tutor (optional)</label>
        <textarea
          className="req-textarea"
          placeholder="Introduce yourself or describe what you'd like to learn..."
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          rows={4}
        />

        <div className="send-request-actions">
          <button className="req-cancel-btn" onClick={() => navigate(-1)}>Cancel</button>
          <button className="req-send-btn" onClick={handleSend} disabled={loading}>
            {loading ? 'Sending...' : 'Send Request'}
          </button>
        </div>
      </div>
    </div>
  );
};
