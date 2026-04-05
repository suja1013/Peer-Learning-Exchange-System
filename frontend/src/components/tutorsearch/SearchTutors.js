import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { tutorsAPI } from '../../services/apiService';
import './SearchTutors.css';

export const SearchTutors = () => {
  const navigate = useNavigate();

  const [query, setQuery]       = useState('');
  const [results, setResults]   = useState([]);
  const [loading, setLoading]   = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError]       = useState('');

  const search = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setError('');
    setSearched(true);
    try {
      const res = await tutorsAPI.searchBySkillName(query.trim());
      setResults(res.data);
    } catch (err) {
      setError('Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="search-page">
      <button className="search-back-btn" onClick={() => navigate('/dashboard')}>
        ← Back
      </button>

      <h2 className="search-title">🔍 Find Tutors</h2>

      <div className="search-bar">
        <input
          className="search-input"
          placeholder="Enter a skill (e.g. React, Python, SQL...)"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && search()}
        />
        <button className="search-btn" onClick={search} disabled={loading}>
          {loading ? 'Searching...' : 'Search'}
        </button>
      </div>

      {error && <div className="search-error">{error}</div>}

      {searched && !loading && (
        <div className="search-results">
          {results.length === 0 ? (
            <div className="search-empty">
              No tutors found for <strong>"{query}"</strong>. Try a different skill.
            </div>
          ) : (
            <>
              <p className="search-count">
                {results.length} tutor{results.length !== 1 ? 's' : ''} found for{' '}
                <strong>"{query}"</strong>
              </p>
              {results.map((item) => (
                <div key={item.id} className="tutor-card">
                  <div className="tutor-card-info">
                    <div className="tutor-name">{item.user.fullName}</div>
                    <div className="tutor-username">@{item.user.username}</div>
                    <div className="tutor-skill-row">
                      <span className="tutor-skill-name">{item.skill.name}</span>
                      <span className={`tutor-badge ${item.experienceLevel?.toLowerCase()}`}>
                        {item.experienceLevel}
                      </span>
                    </div>
                    {item.description && (
                      <div className="tutor-desc">{item.description}</div>
                    )}
                    <div className="tutor-points">⚡ {item.user.activationPoints} pts</div>
                  </div>
                  <button
                    className="tutor-request-btn"
                    onClick={() =>
                      navigate(
                        `/requests/send/${item.user.id}?skillId=${item.skill.id}&skillName=${item.skill.name}`
                      )
                    }
                  >
                    Request Session
                  </button>
                </div>
              ))}
            </>
          )}
        </div>
      )}
    </div>
  );
};