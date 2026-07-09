-- ============================================================
-- 抢答功能模块 - 建表 (MyBatis-Plus 管理, 手动建表)
-- ============================================================

-- 抢答题目表
CREATE TABLE IF NOT EXISTS quiz_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '题目',
    option_a VARCHAR(200) NOT NULL COMMENT 'A选项',
    option_b VARCHAR(200) NOT NULL COMMENT 'B选项',
    option_c VARCHAR(200) COMMENT 'C选项',
    option_d VARCHAR(200) COMMENT 'D选项',
    correct_answer CHAR(1) NOT NULL COMMENT '正确答案(A/B/C/D)',
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING-待开始,ACTIVE-进行中,CLOSED-已结束',
    created_by BIGINT NOT NULL COMMENT '创建人ID(教师)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL COMMENT '开始时间',
    closed_at TIMESTAMP NULL COMMENT '结束时间'
);

-- 抢答记录表
CREATE TABLE IF NOT EXISTS quiz_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    selected_answer CHAR(1) NOT NULL COMMENT '选择的答案(A/B/C/D)',
    is_correct BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否正确',
    submit_time TIMESTAMP(3) NOT NULL COMMENT '提交时间(精确到毫秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 唯一索引防重复提交
    CONSTRAINT uk_question_student UNIQUE (question_id, student_id),
    -- 联合索引优化排名查询
    INDEX idx_question_submit (question_id, submit_time)
);

-- 学生表 (抢答模块专用)
CREATE TABLE IF NOT EXISTS quiz_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    real_name VARCHAR(50) NOT NULL COMMENT '姓名',
    class_name VARCHAR(100) COMMENT '班级',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 抢答模块示例数据
INSERT INTO quiz_student (student_no, real_name, class_name) VALUES
('2024001', '李明', '计算机1班'),
('2024002', '王芳', '计算机1班'),
('2024003', '赵强', '计算机1班');

INSERT INTO quiz_question (title, option_a, option_b, option_c, option_d, correct_answer, status, created_by, created_at) VALUES
('Java中关键字"final"修饰类的作用是什么？', '该类可以被继承', '该类不能被继承', '该类必须实现接口', '该类是抽象的', 'B', 'PENDING', 1, NOW()),
('以下哪个不是MySQL的聚合函数？', 'SUM', 'COUNT', 'JOIN', 'AVG', 'C', 'PENDING', 1, NOW()),
('Spring Boot中@RestController的作用是？', '返回视图', '返回JSON/XML', '返回文本', '返回文件', 'B', 'PENDING', 1, NOW());

-- ============================================================
-- 原有示例数据
-- ============================================================

-- 示例用户 (密码均为 123456)
INSERT INTO users (username, password, real_name, role, created_at) VALUES
('teacher1', '123456', '张老师', 'TEACHER', NOW()),
('student1', '123456', '李明', 'STUDENT', NOW()),
('student2', '123456', '王芳', 'STUDENT', NOW()),
('student3', '123456', '赵强', 'STUDENT', NOW());

-- 示例课程
INSERT INTO courses (name, description, teacher_id, created_at) VALUES
('Java程序设计', '面向对象的Java编程基础课程', 1, NOW()),
('数据结构与算法', '常见数据结构与算法分析', 1, NOW());

-- 学生选课
INSERT INTO course_enrollments (course_id, student_id) VALUES
(1, 2), (1, 3), (1, 4),
(2, 2), (2, 3);

-- 知识点
INSERT INTO knowledge_points (name, description, course_id, created_at) VALUES
('变量与数据类型', 'Java基本数据类型与变量声明', 1, NOW()),
('面向对象基础', '类、对象、继承、多态', 1, NOW()),
('异常处理', 'try-catch-finally与自定义异常', 1, NOW()),
('数组与链表', '数组与链表的实现与比较', 2, NOW()),
('排序算法', '冒泡、快排、归并排序', 2, NOW());

-- 投票会话
INSERT INTO vote_sessions (title, course_id, knowledge_point_id, teacher_id, status, created_at) VALUES
('面向对象概念理解测试', 1, 2, 1, 'ACTIVE', NOW()),
('排序算法掌握度检测', 2, 5, 1, 'ACTIVE', NOW());

-- 投票选项 (A/B/C/D 每题一个正确答案)
INSERT INTO vote_options (text, is_correct, vote_session_id) VALUES
('封装、继承、多态是面向对象的三大特性', TRUE, 1),
('面向对象不需要封装', FALSE, 1),
('Java不支持继承', FALSE, 1),
('多态只能通过接口实现', FALSE, 1);

INSERT INTO vote_options (text, is_correct, vote_session_id) VALUES
('冒泡排序的时间复杂度是O(n²)', TRUE, 2),
('快速排序总是比冒泡排序快', FALSE, 2),
('归并排序的空间复杂度是O(1)', FALSE, 2),
('所有排序算法时间复杂度都是O(n log n)', FALSE, 2);

-- 示例投票记录
INSERT INTO vote_records (vote_session_id, option_id, student_id, created_at) VALUES
(1, 1, 2, NOW()),
(1, 1, 3, NOW()),
(1, 4, 4, NOW()),
(2, 1, 2, NOW()),
(2, 2, 3, NOW());
