/**
 * CVS API Helper - 封装后端 API 调用
 */
const API_BASE = '/api';

async function request(url, options = {}) {
  const config = {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  };
  const resp = await fetch(API_BASE + url, config);
  const data = await resp.json();
  if (data.code !== 200) {
    throw new Error(data.message || '请求失败');
  }
  return data;
}

const API = {
  // 认证
  login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  register: (body) => request('/auth/register', { method: 'POST', body: JSON.stringify(body) }),

  // 课程
  getCourses: (userId, role) => request(`/courses?userId=${userId}&role=${role}`),
  createCourse: (body) => request('/courses', { method: 'POST', body: JSON.stringify(body) }),
  enrollCourse: (courseId, studentId) =>
    request(`/courses/${courseId}/enroll`, { method: 'POST', body: JSON.stringify({ studentId }) }),
  deleteCourse: (courseId, teacherId) =>
    request(`/courses/${courseId}?teacherId=${teacherId}`, { method: 'DELETE' }),

  // 知识点
  getKnowledgePoints: (courseId) => request(`/courses/${courseId}/knowledge-points`),
  addKnowledgePoint: (courseId, body) =>
    request(`/courses/${courseId}/knowledge-points`, { method: 'POST', body: JSON.stringify(body) }),
  deleteKnowledgePoint: (kpId, teacherId) =>
    request(`/knowledge-points/${kpId}?teacherId=${teacherId}`, { method: 'DELETE' }),

  // 投票
  createVote: (body, teacherId) =>
    request(`/vote-sessions?teacherId=${teacherId}`, { method: 'POST', body: JSON.stringify(body) }),
	getVoteDetail: (sessionId, studentId) =>
	  request(`/vote-sessions/${sessionId}${studentId ? '?studentId=' + studentId : ''}`),
  castVote: (sessionId, studentId, optionId) =>
    request(`/vote-sessions/${sessionId}/vote`, { method: 'POST', body: JSON.stringify({ studentId, optionId }) }),
	closeVote: (sessionId, teacherId) =>
	  request(`/vote-sessions/${sessionId}/close?teacherId=${teacherId}`, { method: 'PUT' }),
	getCourseVotes: (courseId) => request(`/vote-sessions/by-course/${courseId}`),
	getVoteRecords: (sessionId, teacherId) =>
	  request(`/vote-sessions/${sessionId}/records?teacherId=${teacherId}`),

  // 投票学生情况
  getVoteStudents: (sessionId) => request(`/vote-sessions/${sessionId}/students`),

  // 统计
  getOverview: (courseId) => request(`/statistics/courses/${courseId}/overview`),
  getKpMastery: (courseId) => request(`/statistics/courses/${courseId}/knowledge-points`),
  getStudentMastery: (courseId) => request(`/statistics/courses/${courseId}/students`),

  // 课堂抢答
  createQuiz: (body) => request('/quiz/create', { method: 'POST', body: JSON.stringify(body) }),
  startQuiz: (id, teacherId) => request(`/quiz/start/${id}?teacherId=${teacherId}`, { method: 'POST' }),
  grabQuiz: (body) => request('/quiz/grab', { method: 'POST', body: JSON.stringify(body) }),
  submitQuiz: (body) => request('/quiz/submit', { method: 'POST', body: JSON.stringify(body) }),
  getQuizStatus: (id) => request(`/quiz/status/${id}`),
  getQuizRank: (id) => request(`/quiz/rank/${id}`),
  getCourseQuizzes: (courseId) => request(`/quiz/by-course/${courseId}`),
};
