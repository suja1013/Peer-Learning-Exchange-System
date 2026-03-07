#!/bin/bash
# git-setup.sh
# Run this once to set up all feature branches for your project
# Make sure you've already done: git init && git add . && git commit -m "base: project setup"

echo "🚀 Setting up Peer Learning Exchange feature branches..."

FEATURES=(
  "feature/F1-user-auth:F1 - User registration, login, roles, activation points"
  "feature/F2-skillset:F2 - Add and manage teaching and learning skills"
  "feature/F3-search-tutors:F3 - Search tutors by skill"
  "feature/F4-manage-requests:F4 - Send, accept, reject session requests"
  "feature/F5-manage-meetings:F5 - Create and manage meeting links"
  "feature/F6-session-completion:F6 - Confirm completion and transfer points"
  "feature/F7-ratings:F7 - Submit ratings and feedback"
  "feature/F8-ai-recommendations:F8 - AI-based tutor recommendations (V2)"
)

for item in "${FEATURES[@]}"; do
  BRANCH="${item%%:*}"
  DESC="${item##*:}"
  git checkout main
  git checkout -b "$BRANCH"
  echo "Created branch: $BRANCH ($DESC)"
done

git checkout main
echo ""
echo "✅ All branches created! Run 'git branch' to see them."
echo ""
echo "📋 Workflow for each feature:"
echo "  git checkout feature/F1-user-auth"
echo "  # make your changes"
echo "  git add ."
echo "  git commit -m 'feat: F1 - description'"
echo "  git push origin feature/F1-user-auth"
echo "  # Create PR and merge into main"
