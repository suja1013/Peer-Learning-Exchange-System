
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 - redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);


// F1: Auth API

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// F2: Skills API

export const skillsAPI = {
  getAllSkills: () => api.get('/skills'),
  searchSkills: (name) => api.get(`/skills/search?name=${name}`),
  getMyTeachingSkills: () => api.get('/skills/my/teaching'),
  getMyLearningSkills: () => api.get('/skills/my/learning'),
  addTeachingSkill: (data) => api.post('/skills/my/teaching', data),
  addLearningSkill: (data) => api.post('/skills/my/learning', data),
  removeTeachingSkill: (skillId) => api.delete(`/skills/my/teaching/${skillId}`),
  removeLearningSkill: (skillId) => api.delete(`/skills/my/learning/${skillId}`),
};


export default api;
