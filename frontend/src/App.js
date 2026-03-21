// src/App.js
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Login, Register } from './components/auth/AuthForms';
import { Dashboard } from './components/dashboard/Dashboard';
import { SkillsManager } from './components/skills/SkillsManager';

// Protected route wrapper
const PrivateRoute = ({ children }) => {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
};

// Layout with back-to-dashboard nav
const PageLayout = ({ children }) => (
  <div>
    <div style={navStyle}>
      <a href="/dashboard" style={linkStyle}>🎓 Peer Learning Exchange</a>
      <nav style={{ display: 'flex', gap: '16px' }}>
        <a href="/skills" style={navLinkStyle}>My Skills</a>
        <a href="/search" style={navLinkStyle}>Find Tutors</a>
        <a href="/requests" style={navLinkStyle}>Requests</a>
        <a href="/meetings" style={navLinkStyle}>Meetings</a>
      </nav>
    </div>
    {children}
  </div>
);

const navStyle = { background: '#fff', borderBottom: '1px solid #e2e8f0', padding: '12px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' };
const linkStyle = { textDecoration: 'none', fontWeight: 700, color: '#4c51bf' };
const navLinkStyle = { color: '#4a5568', textDecoration: 'none', fontSize: '14px', fontWeight: 500 };

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route path="/dashboard" element={
        <PrivateRoute><Dashboard /></PrivateRoute>
      } />

       <Route path="/skills" element={
              <PrivateRoute><PageLayout><SkillsManager /></PageLayout></PrivateRoute>
            } />

      <Route path="/" element={<Navigate to="/dashboard" />} />
      <Route path="*" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
