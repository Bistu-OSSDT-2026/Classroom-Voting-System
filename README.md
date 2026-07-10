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

**Classroom Vote System (CVS)** 是一款面向课堂教学场景的开源工具软件，旨在帮助教师通过**实时投票**和**课堂抢答**快速掌握学生对各知识点的理解程度。系统采用 Spring Boot 三层架构（Controller → Service → Repository），支持教师创建课程知识体系、发起课堂投票与抢答、收集学生答题数据，并以可视化图表呈现各知识点的掌握度统计。

### 核心功能

| 模块 | 功能 | 说明 |
|------|------|------|
| 🔐 用户认证 | 注册/登录 | 教师/学生角色区分，sessionStorage 会话保持 |
| 📚 课程管理 | 课程 CRUD + 选课 | 教师创建课程，学生通过课程 ID 加入 |
| 🏷️ 知识点管理 | 知识树构建 | 为课程添加/删除知识点，作为投票的知识锚点 |
| 📝 课堂投票 | 实时投票 + 反馈 | 教师发起投票（设置选项与正确答案），学生参与后即时显示正误 |
| 👤 实名/匿名投票 | 隐私控制 | 教师可配置实名/匿名模式；学生未投票前完全隐藏票数分布 |
| ⚡ 课堂抢答 | 抢占式答题 | 教师出题→学生抢名额（前3人）→答题→排名展示 |
| 📊 数据统计 | Chart.js 可视化 | 课程概览、知识点掌握度柱状图、学生排名横向图 |

### 核心价值

- 🎯 **精准教学**：实时了解每个知识点的学生掌握情况，针对薄弱环节调整教学策略
- ⚡ **高效互动**：课堂投票 + 抢答替代传统举手/点名，全员参与，数据自动汇总
- 👁️ **隐私保护**：学生未投票前完全看不到票数分布，避免从众效应；支持匿名投票
- 📊 **数据驱动**：Chart.js 可视化图表，掌握度一目了然，教师端根据正确率给出教学建议
- 🆓 **开源免费**：MIT 协议，可自由使用、修改、部署

---

## 🎬 应用场景

| 场景 | 描述 |
|------|------|
| 🏫 **高校课堂** | 教师在讲授某个知识点后发起投票，实时检测学生理解程度 |
| ⚡ **课堂抢答** | 教师出题，学生抢占答题名额（前3名），激发课堂参与感 |
| 📚 **培训教学** | 企业培训师在技术培训中验证学员对关键概念的掌握 |
| 🧪 **翻转课堂** | 课前发布预习投票，课堂针对性讲解薄弱知识点 |
| 📝 **随堂测验** | 替代纸质小测，自动统计正确率并生成掌握度图表 |
| 🎓 **公开课/讲座** | 大规模听众互动，实时了解听众背景和认知水平 |

---

## 🏗️ 产品设计

### 用户角色

| 角色 | 权限 |
|------|------|
| **教师** | 创建/管理课程、知识点；发起/关闭投票；出题/开始抢答；查看所有统计数据、投票明细、抢答排名 |
| **学生** | 加入课程；参与投票（一人一票）；参与抢答（抢占名额→答题）；查看个人答题反馈 |

### 核心流程

```
┌─ 投票流程 ──────────────────────────────────────┐
│ 教师创建课程 → 添加知识点 → 发起投票              │
│   (关联知识点+设置选项+标记正确答案+实名/匿名)      │
│                       ↓                          │
│ 学生加入课程 → 查看投票 → 参与投票（单选）         │
│   （未投票前看不到票数，投票后看到正误反馈）        │
│                       ↓                          │
│ 教师查看投票分布 → 关闭投票 → 统计掌握度 → 图表展示 │
└─────────────────────────────────────────────────┘

┌─ 抢答流程 ──────────────────────────────────────┐
│ 教师创建题目 → 设置选项和正确答案 → 点击「开始」    │
│                       ↓                          │
│ 学生点击「抢答」→ 前3名抢到名额 → 选择答案 → 提交  │
│   （第4人及以后看到"名额已满"提示）               │
│                       ↓                          │
│ 教师实时看到正确答案 → 排行榜展示抢答顺序和姓名     │
└─────────────────────────────────────────────────┘
```

### 功能清单

| 模块 | 功能 | 状态 |
|------|------|------|
| 用户管理 | 注册/登录，教师/学生角色区分 | ✅ |
| 课程管理 | 教师创建课程，学生通过 ID 加入/退课 | ✅ |
| 知识点管理 | 为课程添加/删除知识点，构建知识树 | ✅ |
| 投票管理 | 发起投票、选项设置（含正确答案标记）、实名/匿名切换 | ✅ |
| 投票反馈 | 学生投票后绿/红色正误标记，教师端正确率分档建议 | ✅ |
| 隐私保护 | 学生未投票前隐藏票数分布，仅教师可见完整数据 | ✅ |
| 投票明细 | 教师查看已投/未投学生名单，非匿名时显示每人选项 | ✅ |
| 课堂抢答 | 教师出题→学生抢占名额（前3人）→答题→排行榜 | ✅ |
| 数据统计 | 课程概览、知识点掌握度、学生排名、Chart.js 图表 | ✅ |
| CI/CD | GitHub Actions 自动编译、测试、打包 | ✅ |
| 一键启动 | build.bat/start.bat 自动检测 JDK 21 | ✅ |

---

## 🧱 架构设计

```
┌──────────────────────────────────────────────────────────────┐
│                     前端 (Browser)                            │
│   HTML5 + Vanilla JS + Chart.js 4.4                           │
│   index / login / courses / course-detail / statistics        │
│   api.js (API 封装) + quiz.js (抢答模块)                       │
├──────────────────────────────────────────────────────────────┤
│                   REST API (HTTP/JSON)                        │
├──────────────────────────────────────────────────────────────┤
│              Controller 层 (6 个控制器)                        │
│   AuthController       CourseController                       │
│   KnowledgePointController   VoteController                   │
│   StatisticsController       QuizController (抢答)            │
├──────────────────────────────────────────────────────────────┤
│              Service 层 (6 个业务服务)                         │
│   UserService  CourseService  KnowledgePointService           │
│   VoteService  StatisticsService  QuizService (抢答)          │
├──────────────────────────────────────────────────────────────┤
│            Repository 层 (9 个 JPA 接口)                       │
│   Spring Data JPA + Hibernate ORM                             │
├──────────────────────────────────────────────────────────────┤
│                  Database (H2 / MySQL)                        │
└──────────────────────────────────────────────────────────────┘
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
| Java | JDK 21 LTS | 长期支持版本 |

### 数据库 ER 图

```
users ──1:N──→ courses ──1:N──→ knowledge_points
  │               │                    │
  │               ├──1:N──→ vote_sessions ──1:N──→ vote_options
  │               │               │                    │
  │               │               └──1:N──→ vote_records
  │               │
  │               ├──1:N──→ course_enrollments
  │               │
  │               └──1:N──→ quiz_question ──1:N──→ quiz_record
  └─────────────────────────────────────────────────────────┘
```

---

## 📁 模块划分

```
team-project/
├── pom.xml                              # Maven 项目配置
├── mvnw / mvnw.cmd                      # Maven Wrapper（无需安装 Maven）
├── build.bat                            # 一键构建脚本（自动检测 JDK 21）
├── start.bat / start.sh                 # 一键启动脚本
│
├── src/main/java/com/cvs/
│   ├── CvsApplication.java              # Spring Boot 启动类
│   ├── config/
│   │   └── WebConfig.java               # CORS 跨域配置
│   ├── model/                           # 📦 实体层 (9个)
│   │   ├── User.java                    # 用户实体（教师/学生角色）
│   │   ├── Course.java                  # 课程实体
│   │   ├── CourseEnrollment.java        # 选课关系（唯一约束）
│   │   ├── KnowledgePoint.java          # 知识点实体
│   │   ├── VoteSession.java             # 投票会话（ACTIVE/CLOSED + 匿名标记）
│   │   ├── VoteOption.java              # 投票选项（含正确答案标记）
│   │   ├── VoteRecord.java              # 投票记录（一人一票唯一约束）
│   │   └── quiz/
│   │       ├── QuizQuestion.java        # 抢答题目（PENDING/ACTIVE/CLOSED）
│   │       └── QuizRecord.java          # 抢答记录（抢名额顺序 + 答题结果）
│   ├── repository/                      # 🗄️ 数据访问层 (9个)
│   │   ├── UserRepository.java
│   │   ├── CourseRepository.java
│   │   ├── CourseEnrollmentRepository.java
│   │   ├── KnowledgePointRepository.java
│   │   ├── VoteSessionRepository.java
│   │   ├── VoteOptionRepository.java
│   │   ├── VoteRecordRepository.java
│   │   └── quiz/
│   │       ├── QuizQuestionRepository.java
│   │       └── QuizRecordRepository.java
│   ├── service/                         # 🔧 业务逻辑层 (6个)
│   │   ├── UserService.java             # 用户认证、角色校验
│   │   ├── CourseService.java           # 课程 CRUD、选课管理
│   │   ├── KnowledgePointService.java   # 知识点管理
│   │   ├── VoteService.java             # 投票创建/投票/关闭/统计
│   │   ├── StatisticsService.java       # 掌握度计算、数据分析
│   │   └── quiz/
│   │       └── QuizService.java         # 抢答：出题/开始/抢名额/提交/排名
│   ├── controller/                      # 🌐 REST API 层 (6个)
│   │   ├── AuthController.java          # /api/auth/*
│   │   ├── CourseController.java        # /api/courses/*
│   │   ├── KnowledgePointController.java # /api/courses/{id}/knowledge-points/*
│   │   ├── VoteController.java          # /api/vote-sessions/*
│   │   ├── StatisticsController.java    # /api/statistics/*
│   │   └── quiz/
│   │       └── QuizController.java      # /api/quiz/*
│   └── dto/                             # 📤 数据传输对象 (20+个)
│       ├── ApiResponse.java             # 统一响应格式 {code, message, data}
│       ├── LoginRequest.java / RegisterRequest.java
│       ├── UserVO.java / CourseVO.java / KnowledgePointVO.java
│       ├── CreateVoteRequest.java / VoteRequest.java
│       ├── VoteSessionVO.java / CastVoteResultVO.java
│       ├── VoteRecordVO.java / VoteStudentVO.java
│       ├── StatisticsVO.java            # 嵌套统计视图
│       └── quiz/
│           ├── QuizCreateRequest.java / QuizSubmitRequest.java
│           ├── QuizGrabVO.java / QuizResultVO.java
│           ├── QuizStatusVO.java / QuizRankVO.java
│
├── src/main/resources/
│   ├── application.yml                  # 应用配置（H2/MySQL 切换）
│   ├── data.sql                         # 示例数据（4个用户、2门课、5个知识点）
│   └── static/                          # 🌐 前端页面
│       ├── index.html                   # 入口跳转
│       ├── login.html                   # 登录/注册页面
│       ├── courses.html                 # 课程列表页面
│       ├── course-detail.html           # 课程详情 + 投票 + 抢答页面
│       ├── statistics.html              # 数据统计图表页面（教师专属）
│       ├── css/style.css                # 全局样式表
│       └── js/
│           ├── api.js                   # API 调用封装（全部 25 个端点）
│           └── quiz.js                  # 抢答模块（IIFE 独立模块）
│
└── .github/workflows/
    ├── ci.yml                           # CI: 自动编译 + 测试
    └── deploy.yml                       # CD: 自动打包 + 上传 Artifact
```

---

## 📖 使用指南

### 演示账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| teacher1 | 123456 | 教师 |
| student1 | 123456 | 学生 |
| student2 | 123456 | 学生 |
| student3 | 123456 | 学生 |

### 教师操作流程

**1. 注册/登录** → 打开 http://localhost:8080 ，点击"注册"选择"教师"角色，或使用演示账号 teacher1 登录。

**2. 创建课程** → 在课程列表页点击"创建课程"，输入课程名称和描述。

**3. 添加知识点** → 进入课程详情页，在左侧"知识点"区域添加（如"变量与数据类型"、"面向对象基础"）。

**4. 发起投票** → 点击"发起投票"，填写：
- 投票标题
- 关联知识点
- 设置选项（A/B/C/D），标记其中一个为正确答案
- 选择实名或匿名模式
- 点击创建

**5. 查看投票结果** → 学生投票后，教师端实时显示：
- 每个选项的票数和进度条
- 正确率百分比
- 根据正确率的教学建议（<60% 建议重讲，60-80% 补充讲解，>80% 可继续）
- 已投票/未投票学生名单（实名模式下可见每人所选选项）

**6. 关闭投票** → 点击"关闭投票"，投票不再接受新提交。

**7. 课堂抢答** → 点击"创建抢答"：
- 输入题目和 2-4 个选项，标记正确答案
- 点击"开始"发起抢答
- 学生端前 3 名点击"抢答"获得答题资格
- 教师端实时显示正确答案
- 点击"排行"查看抢答顺序和姓名

**8. 查看统计** → 在课程列表页点击"数据统计"：
- 课程概览（知识点数、投票数、学生数、整体正确率）
- 知识点掌握度柱状图（绿色≥80%，黄色 50-80%，红色<50%）
- 学生掌握度横向排名图

### 学生操作流程

**1. 注册/登录** → 选择"学生"角色注册，或使用 student1~3 登录。

**2. 加入课程** → 点击"加入课程"，输入教师提供的课程 ID。

**3. 参与投票** → 进入课程，在投票列表中点击"进入"：
- 选择其中一个选项
- 点击"提交投票"
- 提交后立即看到绿色（正确）或红色（错误）反馈
- ⚠️ 在投票前看不到任何票数分布，避免从众效应

**4. 参与抢答** → 当教师开始抢答后：
- 点击"抢答"按钮抢占名额（仅前 3 名）
- 抢到后选择答案并提交
- 查看正误反馈和排名

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

> ⚠️ **前置条件**：必须先安装 **JDK 21+**。下载地址：[Adoptium JDK 21](https://adoptium.net/download/)

**Windows 用户：**

```
1. 双击 build.bat 构建项目（首次需下载依赖，约 1-2 分钟）
   └─ 脚本会自动检测 JDK 21 路径
   └─ 如果检测失败，会提示你手动输入 JDK 21 的安装路径
      例如：D:\cursor\jdk21  或  C:\Program Files\Java\jdk-21

2. 构建成功后，双击 start.bat 启动服务
   └─ 同样会自动检测 JDK，检测失败时手动输入路径即可
   └─ 脚本会自动清理端口 8080 的旧进程

3. 浏览器访问 http://localhost:8080
```

**Mac / Linux 用户：**

```bash
# 1. 克隆项目
git clone https://github.com/Bistu-OSSDT-2026/Classroom-Voting-System.git
cd Classroom-Voting-System

# 2. 构建（首次需下载依赖，约 1-2 分钟）
./mvnw package -DskipTests

# 3. 启动
./mvnw spring-boot:run
# 或：java -jar target/cvs-app.jar

# 4. 浏览器访问 http://localhost:8080
```

> 💡 如果不知道 JDK 21 安装在哪里，可以在命令行输入 `where java`（Windows）或 `which java`（Mac/Linux）查看。

---

## 🚀 部署

### 方式一：本地部署

```bash
# 打包 JAR（跳过测试）
./mvnw clean package -DskipTests

# 运行
java -jar target/cvs-app.jar

# 自定义端口
java -jar target/cvs-app.jar --server.port=9090
```

### 方式二：GitHub Actions 自动构建

每次推送代码，GitHub Actions 自动：
1. 编译 + 测试
2. 打包 JAR
3. 上传为 Artifact（可下载）

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
| DELETE | `/api/courses/{id}?teacherId=` | 删除课程（仅创建者） |

### 知识点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/courses/{cid}/knowledge-points` | 知识点列表 |
| POST | `/api/courses/{cid}/knowledge-points` | 添加知识点 `{teacherId, name, description}` |
| DELETE | `/api/knowledge-points/{id}?teacherId=` | 删除知识点（仅创建者） |

### 投票

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/vote-sessions?teacherId=` | 创建投票 `{title, courseId, knowledgePointId, options[], anonymous}` |
| GET | `/api/vote-sessions/{id}?studentId=` | 投票详情（学生身份决定是否可见票数） |
| POST | `/api/vote-sessions/{id}/vote` | 提交投票 `{studentId, optionId}` |
| PUT | `/api/vote-sessions/{id}/close?teacherId=` | 关闭投票 |
| GET | `/api/vote-sessions/{id}/students` | 已投/未投学生名单 |
| GET | `/api/vote-sessions/by-course/{cid}` | 课程投票列表（含正确率） |
| GET | `/api/vote-sessions/{id}/records?teacherId=` | 学生投票明细 |

### 统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/courses/{id}/overview` | 课程掌握度概览 |
| GET | `/api/statistics/courses/{id}/knowledge-points` | 各知识点掌握度 |
| GET | `/api/statistics/courses/{id}/students` | 各学生掌握度排名 |

### 课堂抢答

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/quiz/create` | 创建题目 `{title, options[], correctOption, courseId, teacherId}` |
| POST | `/api/quiz/start/{id}?teacherId=` | 教师开始抢答 |
| POST | `/api/quiz/grab` | 学生抢占名额 `{questionId, studentId}` |
| POST | `/api/quiz/submit` | 提交答案 `{questionId, studentId, chosenOption}` |
| GET | `/api/quiz/status/{id}` | 题目状态（已抢/答对数量） |
| GET | `/api/quiz/rank/{id}` | 抢答排行榜（按抢答顺序） |
| GET | `/api/quiz/by-course/{cid}` | 课程抢答题目列表 |

---

## 🤝 如何为项目做贡献

我们欢迎任何形式的贡献！无论是代码、文档、Bug 报告还是功能建议。

### 贡献流程

```
Fork → Clone → 创建分支 → 修改 → 测试 → Commit → Push → Pull Request
```

### 代码规范

- 遵循 Java 命名规范（驼峰命名）
- Controller → Service → Repository 三层不跨层调用
- 统一使用 `ApiResponse` 封装返回值
- 提交信息格式：`feat:` / `fix:` / `docs:` / `refactor:` / `test:`

### 功能路线图 (Roadmap)

| 优先级 | 功能 | 状态 |
|--------|------|------|
| ~~P0~~ | ~~课堂抢答功能~~ | ✅ 已完成 |
| P0 | 用户认证 Session/JWT 增强 | 📋 待开发 |
| P0 | 单元测试覆盖 Service 层 | 📋 待开发 |
| P1 | 投票支持多选、限时 | 📋 待开发 |
| P1 | 数据导出 Excel/PDF | 📋 待开发 |
| P2 | WebSocket 实时投票推送 | 📋 待开发 |
| P2 | Docker 容器化部署 | 📋 待开发 |
| P3 | 题库管理（预设投票模板） | 📋 待开发 |
| P3 | 学生个人学习报告 | 📋 待开发 |

---

## 👥 核心团队

| 角色 | 成员 |
|------|------|
| 项目负责人、后端开发 | [@lclll-7427](https://github.com/lclll-7427) |
| 核心开发者、前端开发 | [@zcr17](https://github.com/zcr17) |
| 核心开发者、构建脚本 | [@Jerryx6218](https://github.com/Jerryx6218) |
| 核心开发者、文档 | [@wenq07](https://github.com/wenq07) |
| 核心开发者、测试 | [@claire571](https://github.com/claire571) |

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。你可以自由地使用、修改、分发本项目代码，但需保留原作者版权声明。

---

<p align="center">
  <b>如果这个项目对你有帮助，请给一个 ⭐ Star！</b>
</p>
