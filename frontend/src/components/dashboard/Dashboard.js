// src/components/dashboard/Dashboard.js
// Main dashboard after login - shows points, quick actions

import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Dashboard.css';

export const Dashboard = () => {
  const { user, logout } = useAuth();

  return (
    <div className="dashboard-page">
      {/* Navbar */}
      <nav className="dashboard-nav">
        <span className="dashboard-nav-brand">🎓 Peer Learning Exchange</span>
        <div className="dashboard-nav-right">
          <span className="dashboard-points">⚡ {user?.activationPoints} points</span>
          <span className="dashboard-username">@{user?.username}</span>
          <button className="dashboard-logout-btn" onClick={logout}>Logout</button>
        </div>
      </nav>

      <div className="dashboard-content">
        <h2 className="dashboard-welcome">Welcome back! 👋</h2>

        {/* Points Card */}
        <div className="dashboard-points-card">
          <div className="dashboard-points-number">{user?.activationPoints}</div>
          <div className="dashboard-points-label">Activation Points</div>
          <div className="dashboard-points-info">
            Use points to book sessions (20 pts each). Teach others to earn more!
          </div>
        </div>

        {/* Quick Actions Grid */}
        <div className="dashboard-grid">
          {[
            { to: '/skills', icon: '📚', title: 'My Skills', desc: 'Manage skills you teach & want to learn', color: '#ebf4ff' },
            { to: '/search', icon: '🔍', title: 'Find Tutors', desc: 'Search for tutors by skill', color: '#f0fff4' },
            { to: '/requests', icon: '📨', title: 'Session Requests', desc: 'Manage your incoming & outgoing requests', color: '#fffaf0' },
            { to: '/meetings', icon: '🎯', title: 'My Meetings', desc: 'View your scheduled sessions', color: '#faf5ff' },
          ].map(({ to, icon, title, desc, color }) => (
            <Link key={to} to={to} className="dashboard-card" style={{ background: color }}>
              <div className="dashboard-card-icon">{icon}</div>
              <div className="dashboard-card-title">{title}</div>
              <div className="dashboard-card-desc">{desc}</div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};