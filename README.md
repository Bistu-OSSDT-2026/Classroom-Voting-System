<p align="center">
  <h1 align="center">📊 Classroom Vote System (CVS)</h1>
  <p align="center">课堂投票与知识点掌握度分析系统</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?logo=springboot" alt="Spring Boot 3.4">
  <img src="https://img.shields.io/badge/Maven-3.9-blue?logo=apachemaven" alt="Maven">
  <img src="https://img.shields.io/badge/H2-Database-004080?logo=h2" alt="H2">
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License MIT">
  <img src="https://img.shields.io/badge/PRs-welcome-brightgreen" alt="PRs Welcome">
</p>

---

## 📖 项目介绍

**Classroom Vote System (CVS)** 是一款面向课堂教学场景的开源工具软件，旨在帮助教师通过**实时投票**快速掌握学生对各知识点的理解程度。系统采用 Spring Boot 三层架构（Controller → Service → Repository），支持教师创建课程知识体系、发起课堂投票、收集学生答题数据，并以可视化图表呈现各知识点的掌握度统计。

### 核心价值

- 🎯 **精准教学**：实时了解每个知识点的学生掌握情况，针对薄弱环节调整教学策略
- ⚡ **高效互动**：课堂投票替代传统举手/点名，全员参与，数据自动汇总
- 📊 **数据驱动**：Chart.js 可视化图表，掌握度一目了然
- 🆓 **开源免费**：MIT 协议，可自由使用、修改、部署

---

## 🎬 应用场景

| 场景 | 描述 |
|------|------|
| 🏫 **高校课堂** | 教师在讲授某个知识点后发起投票，实时检测学生理解程度 |
| 📚 **培训教学** | 企业培训师在技术培训中验证学员对关键概念的掌握 |
| 🧪 **翻转课堂** | 课前发布预习投票，课堂针对性讲解薄弱知识点 |
| 📝 **随堂测验** | 替代纸质小测，自动统计正确率 |
| 🎓 **公开课/讲座** | 大规模听众互动，实时了解听众背景和认知水平 |

---

## 🏗️ 产品设计 (PRD)

### 用户角色

| 角色 | 权限 |
|------|------|
| **教师** | 创建/管理课程、知识点、发起投票、关闭投票、查看所有统计数据 |
| **学生** | 加入课程、参与投票、查看投票结果 |

### 核心流程

```
教师创建课程 → 添加知识点 → 发起投票(关联知识点+设置正确答案)
                                          ↓
学生加入课程 → 查看投票列表 → 参与投票(选择答案)
                                          ↓
教师关闭投票 → 系统统计掌握度 → 图表可视化展示
```

### 功能清单

| 模块 | 功能 | 状态 |
|------|------|------|
| 用户管理 | 注册/登录，教师/学生角色区分 | ✅ |
| 课程管理 | 教师创建课程，学生通过 ID 加入 | ✅ |
| 知识点管理 | 为课程添加/删除知识点，构建知识树 | ✅ |
| 投票管理 | 发起投票、选项设置、参与投票、关闭投票 | ✅ |
| 数据统计 | 课程概览、知识点掌握度、学生排名、Chart.js 图表 | ✅ |

---

## 🧱 架构设计

```
┌──────────────────────────────────────────────────────┐
│                    前端 (Browser)                     │
│   HTML5 + Vanilla JS + Chart.js                      │
│   login / courses / course-detail / statistics        │
├──────────────────────────────────────────────────────┤
│                  REST API (HTTP/JSON)                 │
├──────────────────────────────────────────────────────┤
│              Controller 层 (5 个控制器)                │
│   AuthController       CourseController               │
│   KnowledgePointController  VoteController            │
│   StatisticsController                                │
├──────────────────────────────────────────────────────┤
│              Service 层 (5 个业务服务)                 │
│   UserService  CourseService  KnowledgePointService   │
│   VoteService  StatisticsService                      │
├──────────────────────────────────────────────────────┤
│            Repository 层 (7 个 JPA 接口)               │
│   Spring Data JPA + Hibernate ORM                     │
├──────────────────────────────────────────────────────┤
│                  Database (H2 / MySQL)                │
└──────────────────────────────────────────────────────┘
```

### 技术选型

| 层级 | 技术 | 选型理由 |
|------|------|----------|
| 后端框架 | Spring Boot 3.4 | 企业级 Java 生态，自动配置，开箱即用 |
| 持久层 | Spring Data JPA + Hibernate | 减少样板代码，自动建表 |
| 数据库 | H2（开发）/ MySQL（生产） | 零配置快速启动 / 生产级可靠性 |
| 构建 | Maven + Wrapper | 无需预装 Maven，`mvnw` 自举 |
| 前端 | 原生 HTML/CSS/JS | 零依赖框架，Spring Boot 直接托管 |
| 图表 | Chart.js 4.x (CDN) | 轻量、美观、支持柱状图/横向图 |
| CI/CD | GitHub Actions | 自动化构建、测试、打包 |
| Java | JDK 21 LTS | 长期支持版本，虚拟线程等新特性 |

### 数据库 ER 图

```
users ──1:N──→ courses ──1:N──→ knowledge_points
  │               │                    │
  │               │                    │
  │           course_enrollments       │
  │               │                    │
  └──1:N──→ vote_records ←── vote_sessions ──1:N──→ vote_options
```

---

## 📁 模块划分

```
team-project/
├── pom.xml                              # Maven 项目配置
├── mvnw / mvnw.cmd                      # Maven Wrapper（无需安装 Maven）
├── build.bat                            # 一键构建脚本
├── start.bat / start.sh                 # 一键启动脚本
│
├── src/main/java/com/cvs/
│   ├── CvsApplication.java              # Spring Boot 启动类
│   ├── config/
│   │   └── WebConfig.java               # CORS 跨域配置
│   ├── model/                           # 📦 实体层 (7个)
│   │   ├── User.java                    # 用户实体（教师/学生角色）
│   │   ├── Course.java                  # 课程实体
│   │   ├── CourseEnrollment.java        # 选课关系
│   │   ├── KnowledgePoint.java          # 知识点实体
│   │   ├── VoteSession.java             # 投票会话（ACTIVE/CLOSED）
│   │   ├── VoteOption.java              # 投票选项（含正确答案标记）
│   │   └── VoteRecord.java              # 投票记录（一人一票）
│   ├── repository/                      # 🗄️ 数据访问层 (7个)
│   │   ├── UserRepository.java
│   │   ├── CourseRepository.java
│   │   ├── CourseEnrollmentRepository.java
│   │   ├── KnowledgePointRepository.java
│   │   ├── VoteSessionRepository.java
│   │   ├── VoteOptionRepository.java
│   │   └── VoteRecordRepository.java
│   ├── service/                         # 🔧 业务逻辑层 (5个)
│   │   ├── UserService.java             # 用户认证、角色校验
│   │   ├── CourseService.java           # 课程 CRUD、选课
│   │   ├── KnowledgePointService.java   # 知识点管理
│   │   ├── VoteService.java             # 投票创建、投票、统计
│   │   └── StatisticsService.java       # 掌握度计算、数据分析
│   ├── controller/                      # 🌐 REST API 层 (5个)
│   │   ├── AuthController.java          # /api/auth/*
│   │   ├── CourseController.java        # /api/courses/*
│   │   ├── KnowledgePointController.java # /api/courses/{id}/knowledge-points/*
│   │   ├── VoteController.java          # /api/vote-sessions/*
│   │   └── StatisticsController.java    # /api/statistics/*
│   └── dto/                             # 📤 数据传输对象 (10个)
│       ├── ApiResponse.java             # 统一响应格式 {code, message, data}
│       ├── LoginRequest.java            # 登录请求
│       ├── RegisterRequest.java         # 注册请求
│       ├── UserVO.java                  # 用户视图对象
│       ├── CourseVO.java                # 课程视图对象
│       ├── KnowledgePointVO.java        # 知识点视图对象
│       ├── CreateVoteRequest.java       # 创建投票请求
│       ├── VoteRequest.java             # 投票请求
│       ├── VoteSessionVO.java           # 投票详情视图
│       └── StatisticsVO.java            # 统计数据视图
│
├── src/main/resources/
│   ├── application.yml                  # 应用配置
│   ├── data.sql                         # 示例数据（演示用）
│   └── static/                          # 🌐 前端页面
│       ├── index.html                   # 入口跳转
│       ├── login.html                   # 登录/注册页面
│       ├── courses.html                 # 课程列表页面
│       ├── course-detail.html           # 课程详情 + 投票页面
│       ├── statistics.html              # 数据统计图表页面
│       ├── css/style.css                # 全局样式表
│       └── js/api.js                    # API 调用封装
│
├── src/test/java/com/cvs/               # 🧪 测试代码（待扩展）
│
└── .github/workflows/
    ├── ci.yml                           # CI: 自动编译测试
    └── deploy.yml                       # CD: 自动打包部署
```

---

## 🛠️ 开发环境设置

### 前置要求

| 工具 | 最低版本 | 说明 |
|------|----------|------|
| JDK | 21+ | [Adoptium Download](https://adoptium.net/download/) |
| Git | 2.x+ | [Git Download](https://git-scm.com/) |
| IDE | — | 推荐 IntelliJ IDEA Community / VS Code |

> 💡 Maven 无需单独安装，项目已包含 Maven Wrapper (`mvnw`)。

### 克隆与启动

```bash
# 1. 克隆项目
git clone https://github.com/lclll-7427/team-project.git
cd team-project

# 2. 构建项目（首次需下载依赖，约 1-2 分钟）
# Windows: 双击 build.bat
# Mac/Linux: ./mvnw package -DskipTests

# 3. 启动服务
# Windows: 双击 start.bat
# Mac/Linux: ./mvnw spring-boot:run

# 4. 浏览器访问
# http://localhost:8080
```

### IDE 导入

**IntelliJ IDEA**:
1. `File` → `Open` → 选择 `team-project` 文件夹
2. IDEA 自动识别 Maven 项目，点击 `Load Maven Project`
3. 运行 `src/main/java/com/cvs/CvsApplication.java` 的 `main` 方法

**VS Code**:
1. 安装 `Extension Pack for Java` 插件
2. `File` → `Open Folder` → 选择 `team-project`
3. 按 `F5` 或点击 `Run` 启动

---

## 🚀 部署

### 方式一：本地部署

```bash
# 打包 JAR
./mvnw clean package -DskipTests

# 运行
java -jar target/cvs-app.jar

# 自定义端口
java -jar target/cvs-app.jar --server.port=9090
```

### 方式二：Docker 部署

```dockerfile
# Dockerfile（待添加）
FROM eclipse-temurin:21-jre
COPY target/cvs-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 方式三：GitHub Actions 自动构建

每次推送代码，GitHub Actions 自动：
1. 编译 + 测试
2. 打包 JAR
3. 上传为 Artifact（可下载）

在 [Actions](https://github.com/lclll-7427/team-project/actions) 页面查看构建结果。

### 数据库切换（H2 → MySQL）

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cvsdb?useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
```

---

## 📡 API 文档

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录 `{username, password}` |
| POST | `/api/auth/register` | 注册 `{username, password, realName, role}` |

### 课程

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/courses?userId=&role=` | 课程列表 |
| POST | `/api/courses` | 创建课程 `{teacherId, name, description}` |
| POST | `/api/courses/{id}/enroll` | 加入课程 `{studentId}` |
| DELETE | `/api/courses/{id}?teacherId=` | 删除课程 |

### 知识点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/courses/{cid}/knowledge-points` | 知识点列表 |
| POST | `/api/courses/{cid}/knowledge-points` | 添加知识点 `{teacherId, name, description}` |
| DELETE | `/api/knowledge-points/{id}?teacherId=` | 删除知识点 |

### 投票

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/vote-sessions?teacherId=` | 创建投票 `{title, courseId, knowledgePointId, options}` |
| GET | `/api/vote-sessions/{id}` | 投票详情（含实时票数） |
| POST | `/api/vote-sessions/{id}/vote` | 提交投票 `{studentId, optionId}` |
| PUT | `/api/vote-sessions/{id}/close?teacherId=` | 关闭投票 |
| GET | `/api/vote-sessions/by-course/{cid}` | 课程投票列表 |

### 统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/courses/{id}/overview` | 课程掌握度概览 |
| GET | `/api/statistics/courses/{id}/knowledge-points` | 各知识点掌握度 |
| GET | `/api/statistics/courses/{id}/students` | 各学生掌握度排名 |

---

## 🤝 如何为项目做贡献

我们欢迎任何形式的贡献！无论是代码、文档、Bug 报告还是功能建议。

### 贡献流程

```
Fork → Clone → 创建分支 → 修改 → 测试 → Commit → Push → Pull Request
```

### 详细步骤

```bash
# 1. Fork 本仓库（点击 GitHub 页面右上角 Fork 按钮）

# 2. Clone 你的 Fork
git clone https://github.com/你的用户名/team-project.git
cd team-project

# 3. 添加上游仓库
git remote add upstream https://github.com/lclll-7427/team-project.git

# 4. 创建功能分支
git checkout -b feature/你的功能名

# 5. 开发 & 测试
# ... 编写代码 ...
./mvnw test

# 6. 提交
git add .
git commit -m "feat: 功能描述"

# 7. 同步上游（防止冲突）
git fetch upstream
git rebase upstream/main

# 8. 推送并创建 PR
git push origin feature/你的功能名
# 在 GitHub 上点击 "New Pull Request"
```

### 代码规范

- 遵循 Java 命名规范（驼峰命名）
- Controller → Service → Repository 三层不跨层调用
- 统一使用 `ApiResponse` 封装返回值
- 提交信息格式：`feat:` / `fix:` / `docs:` / `refactor:` / `test:`

### 功能路线图 (Roadmap)

| 优先级 | 功能 | 状态 |
|--------|------|------|
| P0 | 用户认证 Session/JWT 增强 | 📋 待开发 |
| P0 | 单元测试覆盖 Service 层 | 📋 待开发 |
| P1 | 投票支持多选、限时 | 📋 待开发 |
| P1 | 数据导出 Excel/PDF | 📋 待开发 |
| P2 | WebSocket 实时投票推送 | 📋 待开发 |
| P2 | Docker 容器化部署 | 📋 待开发 |
| P3 | 题库管理（预设投票模板） | 📋 待开发 |
| P3 | 学生个人学习报告 | 📋 待开发 |

---

## 🆘 如何获取帮助

| 渠道 | 说明 |
|------|------|
| 📖 [GitHub Issues](https://github.com/lclll-7427/team-project/issues) | Bug 报告、功能请求 |
| 💬 [GitHub Discussions](https://github.com/lclll-7427/team-project/discussions) | 技术讨论、使用问题 |
| 📧 Email | 通过 GitHub Profile 联系维护者 |

### 常见问题

**Q: 启动报 `Port 8080 was already in use`？**
A: 重新双击 `start.bat`（已内置自动杀旧进程）。或手动：`netstat -ano | findstr :8080` → `taskkill /PID xxx /F`

**Q: 前端页面中文乱码？**
A: 检查浏览器编码设置为 UTF-8。

**Q: 如何重置演示数据？**
A: 重启服务即可（H2 内存数据库自动重置为 `data.sql`）。

---

## 👥 核心团队

| 角色 | 成员 |
|------|------|
| 项目负责人 | [@lclll-7427](https://github.com/lclll-7427) |
| 核心开发者 | [@Jerryx6218](https://github.com/Jerryx6218) |
| 贡献者 | [查看贡献者列表](https://github.com/lclll-7427/team-project/graphs/contributors) |

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。你可以自由地使用、修改、分发本项目代码，但需保留原作者版权声明。

---

<p align="center">
  <b>如果这个项目对你有帮助，请给一个 ⭐ Star！</b>
</p>
