
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { recommendationsAPI } from '../../services/apiService';
import './AIRecommendations.css';

export const AIRecommendations = () => {
  const navigate = useNavigate();

  const [query, setQuery]       = useState('');
  const [results, setResults]   = useState([]);
  const [loading, setLoading]   = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError]       = useState('');

  const getRecommendations = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setError('');
    setSearched(true);
    try {
      const res = await recommendationsAPI.get(query.trim(), 10);
      setResults(res.data);
    } catch (err) {
      setError('Failed to get recommendations. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Score bar — score is 0.0–1.0, display as percentage
  const ScoreBar = ({ score }) => {
    const pct = Math.round(score * 100);
    return (
      <div className="ai-score-bar-track">
        <div className="ai-score-bar-fill" style={{ width: `${pct}%` }} />
        <span className="ai-score-bar-label">{pct}% match</span>
      </div>
    );
  };

  // Star display using avgRating (0–5)
  const Stars = ({ avg }) => {
    if (!avg || avg === 0) return <span className="ai-no-rating">No ratings yet</span>;
    return (
      <span className="ai-stars">
        {[1, 2, 3, 4, 5].map((n) => (
          <span key={n} className={n <= Math.round(avg) ? 'ai-star-on' : 'ai-star-off'}>
            ★
          </span>
        ))}
        <span className="ai-star-value">{avg.toFixed(1)}</span>
      </span>
    );
  };

  return (
    <div className="ai-page">
      <button className="ai-back-btn" onClick={() => navigate('/dashboard')}>
        ← Back
      </button>

      <div className="ai-header">
        <h2 className="ai-title">🤖 AI Tutor Recommendations</h2>
        <p className="ai-subtitle">
          Tutors are ranked by ratings, completed sessions, and experience level.
          Related skills are also included — searching "React" surfaces HTML, CSS
          and JavaScript tutors too.
        </p>
      </div>

      {/* Search bar */}
      <div className="ai-search-bar">
        <input
          className="ai-search-input"
          placeholder="Enter a skill (e.g. React, Python, Java...)"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && getRecommendations()}
        />
        <button
          className="ai-search-btn"
          onClick={getRecommendations}
          disabled={loading}
        >
          {loading ? 'Analysing...' : '✨ Get Recommendations'}
        </button>
      </div>

      {error && <div className="ai-error">{error}</div>}

      {loading && (
        <div className="ai-loading">
          <div className="ai-spinner" />
          <p>AI is ranking tutors for you...</p>
        </div>
      )}

      {searched && !loading && results.length === 0 && (
        <div className="ai-empty">
          No tutors found for <strong>"{query}"</strong>. Try a broader skill name.
        </div>
      )}

      {!loading && results.length > 0 && (
        <>
          <p className="ai-results-count">
            Found <strong>{results.length}</strong> recommended tutors for{' '}
            <strong>"{query}"</strong>
          </p>

          <div className="ai-results">
            {results.map((tutor, index) => (
              <div key={tutor.tutorId} className="ai-card">

                {/* Rank badge */}
                <div className={`ai-rank ai-rank-${index < 3 ? index + 1 : 'other'}`}>
                  #{index + 1}
                </div>

                <div className="ai-card-body">
                  {/* Top row: name + badges */}
                  <div className="ai-card-top">
                    <div>
                      <div className="ai-tutor-name">{tutor.tutorFullName}</div>
                      <div className="ai-tutor-username">@{tutor.tutorUsername}</div>
                    </div>
                    <div className="ai-badges">
                      {tutor.exactMatch ? (
                        <span className="ai-badge-exact">✓ Exact Match</span>
                      ) : (
                        <span className="ai-badge-related">
                          ~ Related: {tutor.matchedSkill}
                        </span>
                      )}
                      <span className={`ai-badge-exp ${tutor.experienceLevel?.toLowerCase()}`}>
                        {tutor.experienceLevel}
                      </span>
                    </div>
                  </div>

                  {/* AI score bar */}
                  <ScoreBar score={tutor.score} />

                  {/* Meta row */}
                  <div className="ai-meta">
                    <span className="ai-skill-name">🎯 {tutor.skillName}</span>
                    <Stars avg={tutor.avgRating} />
                    <span className="ai-sessions">
                      ✅ {tutor.completedSessions} session{tutor.completedSessions !== 1 ? 's' : ''}
                    </span>
                    <span className="ai-points">⚡ {tutor.tutorPoints} pts</span>
                  </div>

                  {tutor.description && (
                    <div className="ai-desc">{tutor.description}</div>
                  )}
                </div>

                {/* Request session button */}
                <button
                  className="ai-request-btn"
                  onClick={() =>
                    navigate(
                      `/requests/send/${tutor.tutorId}?skillId=${tutor.skillId}&skillName=${tutor.skillName}`
                    )
                  }
                >
                  Request Session
                </button>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};