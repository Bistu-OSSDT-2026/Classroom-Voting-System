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
INSERT INTO vote_sessions (title, course_id, knowledge_point_id, teacher_id, status, anonymous, created_at) VALUES
('面向对象概念理解测试', 1, 2, 1, 'ACTIVE', FALSE, NOW()),
('排序算法掌握度检测', 2, 5, 1, 'ACTIVE', FALSE, NOW());

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
