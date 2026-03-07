// src/components/auth/Login.js
// F1: Login Page

import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './AuthForms.css';

export const Login = () => {
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await login(form.email, form.password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-title">🎓 Peer Learning Exchange</h2>
        <h3 className="auth-subtitle">Sign In</h3>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="auth-field">
            <label className="auth-label">Email</label>
            <input
              className="auth-input"
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
              placeholder="you@example.com"
            />
          </div>
          <div className="auth-field">
            <label className="auth-label">Password</label>
            <input
              className="auth-input"
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
              placeholder="••••••••"
            />
          </div>
          <button className="auth-button" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="auth-link">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
};

// src/components/auth/Register.js
export const Register = () => {
  const [form, setForm] = useState({
    username: '', email: '', password: '', fullName: '', role: 'BOTH'
  });
  const [error, setError] = useState('');
  const { register, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await register(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-title">🎓 Peer Learning Exchange</h2>
        <h3 className="auth-subtitle">Create Account</h3>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          {[
            { label: 'Full Name', key: 'fullName', type: 'text', placeholder: 'Jane Doe' },
            { label: 'Username', key: 'username', type: 'text', placeholder: 'janedoe' },
            { label: 'Email', key: 'email', type: 'email', placeholder: 'you@example.com' },
            { label: 'Password', key: 'password', type: 'password', placeholder: '6+ characters' },
          ].map(({ label, key, type, placeholder }) => (
            <div key={key} className="auth-field">
              <label className="auth-label">{label}</label>
              <input
                className="auth-input"
                type={type}
                value={form[key]}
                onChange={(e) => setForm({ ...form, [key]: e.target.value })}
                required
                placeholder={placeholder}
              />
            </div>
          ))}

          <div className="auth-field">
            <label className="auth-label">Primary Role</label>
            <select
              className="auth-input"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
            >
              <option value="BOTH">Both (Teach & Learn)</option>
              <option value="TUTOR">Primarily Tutor</option>
              <option value="LEARNER">Primarily Learner</option>
            </select>
          </div>

          <div className="auth-points-note">
            🎉 You'll receive <strong>100 activation points</strong> on registration!
          </div>

          <button className="auth-button" type="submit" disabled={loading}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <p className="auth-link">
          Already have an account? <Link to="/login">Sign In</Link>
        </p>
      </div>
    </div>
  );
};