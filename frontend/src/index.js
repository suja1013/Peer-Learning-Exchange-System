import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Global styles reset
const globalStyles = `
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f7fafc; color: #2d3748; }
  a { color: inherit; }
  input, select, textarea, button { font-family: inherit; }
`;

const styleSheet = document.createElement('style');
styleSheet.textContent = globalStyles;
document.head.appendChild(styleSheet);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
