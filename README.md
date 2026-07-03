# Classroom Vote System (CVS)

课堂实时投票系统 - 基于 Vite 构建的轻量级前端应用。

## 🚀 快速开始

```bash
# 安装依赖
npm install

# 本地开发 (localhost:3000)
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

## ✅ 检查与测试

```bash
# 代码风格检查
npm run lint

# 运行单元测试
npm test
```

## 📁 项目结构

```
team-project/
├── index.html              # 入口页面
├── package.json            # 项目配置
├── vite.config.js          # Vite 构建配置
├── eslint.config.js        # ESLint 规则
├── deploy.sh               # 一键部署脚本 (本地)
├── src/
│   ├── main.js             # 应用入口
│   ├── app.js              # 投票核心逻辑
│   ├── style.css           # 全局样式
│   └── __tests__/
│       └── app.test.js     # 单元测试
└── .github/workflows/
    ├── ci.yml              # CI: lint → test → build
    └── deploy.yml          # CD: 自动部署到 GitHub Pages
```

## 🔄 GitHub Actions 自动化

| 工作流 | 触发条件 | 功能 |
|--------|----------|------|
| **CI** (`ci.yml`) | push / PR 到 main | 代码检查 → 测试 → 构建 → 上传产物 |
| **Deploy** (`deploy.yml`) | push 到 main | 检查 → 测试 → 构建 → 发布到 GitHub Pages |

每次推送代码到 `main` 分支，GitHub Actions 会自动：
1. 安装依赖
2. 运行 ESLint 检查
3. 执行单元测试
4. 构建生产版本
5. 部署到 GitHub Pages

## 🔧 本地部署

```bash
# 一键提交并推送
./deploy.sh "更新投票功能"

# 仅提交不推送
./deploy.sh "修复bug" --no-push
```

## 🌐 在线访问

部署后访问: `https://<你的用户名>.github.io/team-project/`
