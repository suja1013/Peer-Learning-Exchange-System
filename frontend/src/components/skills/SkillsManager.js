import React, { useState, useEffect } from 'react';
import { skillsAPI } from '../../services/apiService';
import './SkillsManager.css';

export const SkillsManager = () => {
  const [teachingSkills, setTeachingSkills] = useState([]);
  const [learningSkills, setLearningSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('teaching');
  const [skillName, setSkillName] = useState('');
  const [experienceLevel, setExperienceLevel] = useState('INTERMEDIATE');
  const [description, setDescription] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    const [teaching, learning] = await Promise.all([
      skillsAPI.getMyTeachingSkills(),
      skillsAPI.getMyLearningSkills(),
    ]);
    setTeachingSkills(teaching.data);
    setLearningSkills(learning.data);
    setLoading(false);
  };

  const addTeaching = async () => {
    if (!skillName.trim()) return;
    await skillsAPI.addTeachingSkill({ skillName: skillName.trim(), experienceLevel, description });
    setSkillName('');
    setDescription('');
    loadData();
  };

  const addLearning = async () => {
    if (!skillName.trim()) return;
    await skillsAPI.addLearningSkill({ skillName: skillName.trim() });
    setSkillName('');
    loadData();
  };

  const removeTeaching = async (skillId) => {
    await skillsAPI.removeTeachingSkill(skillId);
    loadData();
  };

  const removeLearning = async (skillId) => {
    await skillsAPI.removeLearningSkill(skillId);
    loadData();
  };

  if (loading) return <div className="skills-loading">Loading skills...</div>;

  return (
    <div className="skills-page">
      <h2 className="skills-title">📚 My Skills</h2>

      {/* Tabs */}
      <div className="skills-tabs">
        {['teaching', 'learning'].map((tab) => (
          <button
            key={tab}
            className={`skills-tab${activeTab === tab ? ' active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab === 'teaching' ? '👨‍🏫 Skills I Teach' : '🎓 Skills I Want to Learn'}
          </button>
        ))}
      </div>

      {/* Add Skill Section */}
      <div className="skills-add-section">
        <h3 className="skills-section-title">
          Add a {activeTab === 'teaching' ? 'Teaching' : 'Learning'} Skill
        </h3>
        <div className="skills-add-row">
          <input
            className="skills-input"
            placeholder="Enter skill name (e.g. React, Python...)"
            value={skillName}
            onChange={(e) => setSkillName(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && (activeTab === 'teaching' ? addTeaching() : addLearning())}
          />

          {activeTab === 'teaching' && (
            <>
              <select
                className="skills-select"
                value={experienceLevel}
                onChange={(e) => setExperienceLevel(e.target.value)}
              >
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="EXPERT">Expert</option>
              </select>
              <input
                className="skills-input"
                placeholder="Short description (optional)"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </>
          )}

          <button
            className="skills-add-btn"
            onClick={activeTab === 'teaching' ? addTeaching : addLearning}
          >
            + Add
          </button>
        </div>
      </div>

      {/* Skills List */}
      <div className="skills-list">
        {(activeTab === 'teaching' ? teachingSkills : learningSkills).length === 0 ? (
          <div className="skills-empty">No skills added yet. Add your first skill above!</div>
        ) : (
          (activeTab === 'teaching' ? teachingSkills : learningSkills).map((item) => (
            <div key={item.id} className="skills-item">
              <div className="skills-item-info">
                <strong>{item.skill.name}</strong>
                {item.experienceLevel && (
                  <span className={`skills-badge ${item.experienceLevel.toLowerCase()}`}>
                    {item.experienceLevel}
                  </span>
                )}
                {item.description && (
                  <span className="skills-desc">{item.description}</span>
                )}
              </div>
              <button
                className="skills-remove-btn"
                onClick={() =>
                  activeTab === 'teaching'
                    ? removeTeaching(item.skill.id)
                    : removeLearning(item.skill.id)
                }
              >
                Remove
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};