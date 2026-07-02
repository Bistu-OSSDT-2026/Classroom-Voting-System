#!/usr/bin/env bash
# 一键提交并推送到 GitHub 仓库 (origin/main)
# 用法:
#   ./deploy.sh                      # 使用默认 commit 消息
#   ./deploy.sh "修复登录bug"         # 自定义 commit 消息
#   ./deploy.sh "msg" --no-push      # 只提交不推送

set -euo pipefail

# 切到脚本所在目录(即仓库根目录)
cd "$(dirname "$0")"

DEFAULT_MSG="update: $(date '+%Y-%m-%d %H:%M:%S')"
COMMIT_MSG="${1:-$DEFAULT_MSG}"
DO_PUSH=1

# 解析开关
for arg in "$@"; do
  case "$arg" in
    --no-push) DO_PUSH=0 ;;
    --push)    DO_PUSH=1 ;;
  esac
done

# 基本检查
if [ ! -d .git ]; then
  echo "❌ 当前目录不是 git 仓库: $(pwd)"
  exit 1
fi

if ! git remote get-url origin >/dev/null 2>&1; then
  echo "❌ 未配置 origin 远程仓库"
  exit 1
fi

REMOTE_URL=$(git remote get-url origin)
BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "📍 仓库:  $REMOTE_URL"
echo "🌿 分支:  $BRANCH"
echo "📝 消息:  $COMMIT_MSG"
echo "────────────────────────────────"

# 1. 暂存所有变更(包含新增/删除/修改),排除本地敏感文件靠 .gitignore
git add -A

# 没有变更就直接结束
if git diff --cached --quiet; then
  echo "ℹ️  没有变更,无需提交"
  exit 0
fi

# 显示将要提交的内容概览
echo "📦 将提交以下变更:"
git diff --cached --stat
echo "────────────────────────────────"

# 2. 提交
git commit -m "$COMMIT_MSG"

# 3. 推送
if [ "$DO_PUSH" -eq 1 ]; then
  echo "🚀 推送到 $REMOTE_URL ..."
  git push origin "$BRANCH"
  echo "✅ 推送完成"
else
  echo "⏭️  已跳过推送(--no-push)"
fi
