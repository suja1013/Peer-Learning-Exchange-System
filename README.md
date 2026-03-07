# Peer Learning Exchange System

A web-based platform connecting students who want to learn skills from fellow peers.

## Tech Stack
- **Backend:** Java 17 + Spring Boot 3.x
- **Frontend:** React.js 18 + Tailwind CSS
- **Database:** MySQL 8.x
- **Auth:** JWT (JSON Web Tokens)

---

## Git Branch Strategy

Each feature maps to its own branch. Merge into `master` after completion.

| Branch | Feature ID | Description |
|--------|------------|-------------|
| `main` | BASE | Base project setup (Spring Boot + React + MySQL) |
| `feature/F1-user-auth` | F1 | User Registration, Login, Roles (Tutor/Learner), Activation Points |
| `feature/F2-skillset` | F2 | Add/manage skills to teach and learn |
| `feature/F3-search-tutors` | F3 | Search tutors by skill |
| `feature/F4-manage-requests` | F4 | Send/Accept/Reject session requests |
| `feature/F5-manage-meetings` | F5 | Tutor sends meeting info/link |
| `feature/F6-session-completion` | F6 | Confirm session & transfer points |
| `feature/F7-ratings` | F7 | Submit ratings & feedback |
| `feature/F8-ai-recommendations` | F8 | AI-based tutor recommendations (V2) |

---

## Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+

### Database Setup
```sql
CREATE DATABASE peer_learning_exchange;
CREATE USER 'ples_user'@'localhost' IDENTIFIED BY 'ples_password';
GRANT ALL PRIVILEGES ON peer_learning_exchange.* TO 'ples_user'@'localhost';
FLUSH PRIVILEGES;
```

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm start
# Runs on http://localhost:3000
```

---

## Git Workflow
```bash
# Start a new feature
git checkout main
git pull origin main
git checkout -b feature/F1-user-auth

# After feature is complete
git add .
git commit -m "feat: F1 - User authentication and registration"
git push origin feature/F1-user-auth

# Create PR and merge into main
git checkout main
git merge feature/F1-user-auth
git push origin main
```
