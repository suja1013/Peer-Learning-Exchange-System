// src/components/ratings/TutorRatings.js

import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ratingsAPI } from '../../services/apiService';
import './Ratings.css';

// ── Single session rating view (tutor clicks "View Rating" on a specific session) ──
export const SessionRating = () => {
  const { meetingId } = useParams();
  const navigate      = useNavigate();

  const [rating, setRating]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');

  useEffect(() => {
    ratingsAPI.checkByMeeting(meetingId)
      .then((res) => setRating(res.data))
      .catch(() => setError('No rating has been submitted for this session yet.'))
      .finally(() => setLoading(false));
  }, [meetingId]);

  const StarRow = ({ count }) => (
    <div className="tr-stars">
      {[1, 2, 3, 4, 5].map((n) => (
        <span key={n} className={n <= count ? 'tr-star-on' : 'tr-star-off'}>★</span>
      ))}
    </div>
  );

  if (loading) return <div className="ratings-page"><p>Loading...</p></div>;

  return (
    <div className="ratings-page">
      <button className="ratings-back-btn" onClick={() => navigate('/requests')}>← Back</button>
      <h2 className="ratings-title">⭐ Session Rating</h2>

      {error ? (
        <div className="tr-avg-card" style={{ background: '#a0aec0' }}>
          <p style={{ margin: 0, fontSize: 16 }}>{error}</p>
        </div>
      ) : (
        <div className="tr-card" style={{ marginTop: 8 }}>
          <div className="tr-card-header">
            <div>
              <div className="tr-learner-name">
                {rating.learner?.fullName || rating.learner?.email || 'Learner'}
              </div>
              <div className="tr-skill">
                🎯 {rating.meeting?.sessionRequest?.skill?.name || 'Session'}
              </div>
              <div className="tr-date">
                {new Date(rating.createdAt).toLocaleDateString('en-US', {
                  year: 'numeric', month: 'short', day: 'numeric',
                })}
              </div>
            </div>
            <StarRow count={rating.rating} />
          </div>
          {rating.feedback && (
            <div className="tr-feedback">"{rating.feedback}"</div>
          )}
        </div>
      )}
    </div>
  );
};

// ── All ratings for a tutor (used on tutor profile / AI recommendations page) ──
export const TutorRatings = () => {
  const { tutorId } = useParams();
  const navigate    = useNavigate();

  const [ratings, setRatings]   = useState([]);
  const [average, setAverage]   = useState(null);
  const [loading, setLoading]   = useState(true);
  const [error, setError]       = useState('');

  useEffect(() => {
    const fetchRatings = async () => {
      try {
        const [ratingsRes, avgRes] = await Promise.all([
          ratingsAPI.getTutorRatings(tutorId),
          ratingsAPI.getTutorAvg(tutorId),
        ]);
        setRatings(ratingsRes.data || []);
        setAverage(avgRes.data);
      } catch (err) {
        setError('Failed to load ratings.');
      } finally {
        setLoading(false);
      }
    };
    fetchRatings();
  }, [tutorId]);

  const StarRow = ({ count }) => (
    <div className="tr-stars">
      {[1, 2, 3, 4, 5].map((n) => (
        <span key={n} className={n <= count ? 'tr-star-on' : 'tr-star-off'}>★</span>
      ))}
    </div>
  );

  if (loading) return <div className="ratings-page"><p>Loading ratings...</p></div>;

  return (
    <div className="ratings-page">
      <button className="ratings-back-btn" onClick={() => navigate(-1)}>← Back</button>
      <h2 className="ratings-title">⭐ Tutor Ratings</h2>

      {error && <div className="ratings-error">{error}</div>}

      <div className="tr-avg-card">
        {ratings.length === 0 ? (
          <p className="tr-no-ratings">No ratings yet.</p>
        ) : (
          <>
            <div className="tr-avg-number">{Number(average).toFixed(1)}</div>
            <StarRow count={Math.round(average)} />
            <div className="tr-avg-label">
              Average from {ratings.length} session{ratings.length !== 1 ? 's' : ''}
            </div>
          </>
        )}
      </div>

      <div className="tr-list">
        {ratings.map((r) => (
          <div key={r.id} className="tr-card">
            <div className="tr-card-header">
              <div>
                <div className="tr-learner-name">
                  {r.learner?.fullName || r.learner?.email || 'Learner'}
                </div>
                <div className="tr-skill">
                  🎯 {r.meeting?.sessionRequest?.skill?.name || 'Session'}
                </div>
              </div>
              <StarRow count={r.rating} />
            </div>
            {r.feedback && (
              <div className="tr-feedback">"{r.feedback}"</div>
            )}
            <div className="tr-date">
              {new Date(r.createdAt).toLocaleDateString('en-US', {
                year: 'numeric', month: 'short', day: 'numeric',
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};