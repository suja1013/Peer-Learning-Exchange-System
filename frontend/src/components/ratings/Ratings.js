
import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ratingsAPI } from '../../services/apiService';
import './Ratings.css';

export const SubmitRating = () => {
  const [searchParams] = useSearchParams();
  const meetingId      = searchParams.get('meetingId');
  const navigate       = useNavigate();

  const [stars, setStars]     = useState(0);
  const [hovered, setHovered] = useState(0);
  const [feedback, setFeedback] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleSubmit = async () => {
    if (stars === 0) {
      setError('Please select a star rating.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await ratingsAPI.submit({
        meetingId: Number(meetingId),
        rating: stars,
        feedback,
      });
      navigate('/requests');
    } catch (err) {
      setError(err.response?.data || 'Failed to submit rating.');
    } finally {
      setLoading(false);
    }
  };

  const starLabels = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'];

  return (
    <div className="ratings-page">
      <button className="ratings-back-btn" onClick={() => navigate('/requests')}>← Back</button>
      <h2 className="ratings-title">⭐ Rate Your Session</h2>

      <div className="ratings-card">
        {error && <div className="ratings-error">{error}</div>}

        <p className="ratings-prompt">How would you rate this session?</p>

        <div className="star-row">
          {[1, 2, 3, 4, 5].map((n) => (
            <button
              key={n}
              className={`star-btn ${n <= (hovered || stars) ? 'star-filled' : ''}`}
              onClick={() => setStars(n)}
              onMouseEnter={() => setHovered(n)}
              onMouseLeave={() => setHovered(0)}
            >
              ★
            </button>
          ))}
        </div>

        {(hovered || stars) > 0 && (
          <div className="star-label">{starLabels[hovered || stars]}</div>
        )}

        <label className="ratings-label">Feedback (optional)</label>
        <textarea
          className="ratings-textarea"
          placeholder="Share your experience with this tutor..."
          value={feedback}
          onChange={(e) => setFeedback(e.target.value)}
          rows={4}
        />

        <div className="ratings-actions">
          <button className="ratings-cancel-btn" onClick={() => navigate('/requests')}>
            Skip
          </button>
          <button className="ratings-submit-btn" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Submitting...' : 'Submit Rating'}
          </button>
        </div>
      </div>
    </div>
  );
};
