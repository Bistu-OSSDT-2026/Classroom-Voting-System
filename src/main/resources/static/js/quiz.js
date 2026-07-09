/**
 * 课堂抢答 - 独立 JS 模块
 * 流程：抢名额（前3人）→ 提交答案
 * 依赖：api.js（提供 API 对象）
 * 变量：window._cvsUser, window._cvsCourseId（由 course-detail.html 设置）
 */
(function() {
  const user = window._cvsUser;
  const courseId = window._cvsCourseId;
  if (!user || !courseId) return;

  const isTeacher = user.role === 'TEACHER';

  // ============================================================
  // 加载抢答题目列表
  // ============================================================
  async function loadQuiz() {
    try {
      const quizAddBtn = document.getElementById('quiz-add-btn');
      if (quizAddBtn && isTeacher) {
        quizAddBtn.classList.remove('hidden');
      }

      const res = await API.getCourseQuizzes(courseId);
      const list = document.getElementById('quiz-list');
      if (!list) return;

      const qs = res.data || [];
      if (qs.length === 0) {
        list.innerHTML = '<p style="color:var(--text-light)">暂无抢答题目，点击右上角「创建抢答」发起</p>';
        return;
      }

      list.innerHTML = qs.map(q => {
        const opts = parseOptions(q.options);
        const optsHtml = opts.map(o => `<span style="margin-right:0.5rem;font-size:0.85rem">${escHtml(o)}</span>`).join('');

        const isActive = q.status === 'ACTIVE';
        const statusText = isActive ? '🔥 进行中' : q.status === 'PENDING' ? '📋 待开始' : '✅ 已结束';
        const statusClass = isActive ? 'badge-active' : q.status === 'PENDING' ? 'badge badge-active' : 'badge-closed';

        let btns = `<button class="btn btn-outline btn-sm" onclick="showQuizDetail(${q.id})">详情</button>`;
        if (isTeacher && q.status === 'PENDING') {
          btns += `<button class="btn btn-success btn-sm" onclick="startQuiz(${q.id})">▶ 开始</button>`;
        }
        btns += `<button class="btn btn-outline btn-sm" onclick="showQuizRank(${q.id})">🏆 排行</button>`;

        return `<div class="list-item">
          <div>
            <strong>${escHtml(q.title)}</strong>
            <div style="font-size:0.8rem;color:var(--text-light);margin-top:0.2rem">${optsHtml}</div>
          </div>
          <div style="display:flex;gap:0.3rem;align-items:center;flex-shrink:0">
            <span class="badge ${statusClass}">${statusText}</span>
            ${btns}
          </div>
        </div>`;
      }).join('');
    } catch (e) {
      const list = document.getElementById('quiz-list');
      if (list) list.innerHTML = '<p style="color:var(--text-light)">加载失败</p>';
    }
  }

  function parseOptions(json) {
    try { return JSON.parse(json); } catch(e) { return []; }
  }

  function escHtml(s) {
    const d = document.createElement('div');
    d.textContent = s;
    return d.innerHTML;
  }

  // ============================================================
  // 创建抢答题目弹窗
  // ============================================================
  window.showCreateQuizModal = function() {
    const overlay = document.getElementById('modal-overlay');
    const content = document.getElementById('modal-content');
    if (!overlay || !content) return;
    overlay.classList.remove('hidden');
    content.innerHTML = `
      <h3>⚡ 创建抢答题目</h3>
      <div class="form-group"><label>题目</label><input id="q-title" placeholder="输入抢答题目"></div>
      <div class="form-group"><label>选项 A</label><input id="q-opt-a" placeholder="选项A内容"></div>
      <div class="form-group"><label>选项 B</label><input id="q-opt-b" placeholder="选项B内容"></div>
      <div class="form-group"><label>选项 C (可选)</label><input id="q-opt-c" placeholder="选项C内容"></div>
      <div class="form-group"><label>选项 D (可选)</label><input id="q-opt-d" placeholder="选项D内容"></div>
      <div class="form-group"><label>正确答案</label>
        <select id="q-correct">
          <option value="A">A</option><option value="B">B</option><option value="C">C</option><option value="D">D</option>
        </select>
      </div>
      <div style="display:flex;gap:0.5rem;justify-content:flex-end">
        <button class="btn btn-outline btn-sm" onclick="closeModal()">取消</button>
        <button class="btn btn-primary btn-sm" onclick="createQuiz()">创建</button>
      </div>
    `;
  };

  window.createQuiz = async function() {
    const title = document.getElementById('q-title').value.trim();
    const optA = document.getElementById('q-opt-a').value.trim();
    const optB = document.getElementById('q-opt-b').value.trim();
    const optC = document.getElementById('q-opt-c').value.trim();
    const optD = document.getElementById('q-opt-d').value.trim();
    const correctOption = document.getElementById('q-correct').value;
    if (!title || !optA || !optB) { alert('请填写题目和至少A、B两个选项'); return; }
    const options = [optA, optB, optC, optD]
      .map((v, i) => v ? (String.fromCharCode(65 + i) + '. ' + v) : null)
      .filter(Boolean);
    try {
      await API.createQuiz({ title, options, correctOption, courseId, teacherId: user.id });
      document.getElementById('modal-overlay').classList.add('hidden');
      alert('创建成功！');
      loadQuiz();
    } catch (e) { alert(e.message); }
  };

  // ============================================================
  // 开始抢答
  // ============================================================
  window.startQuiz = async function(id) {
    if (!confirm('确定开始抢答？')) return;
    try {
      await API.startQuiz(id, user.id);
      alert('抢答已开始！前3名抢到的学生可答题。');
      loadQuiz();
    } catch (e) { alert(e.message); }
  };

  // ============================================================
  // 查看详情（含抢名额 + 答题入口）
  // ============================================================
  let _chosenOption = null;

  window.showQuizDetail = async function(id) {
    try {
      const res = await API.getQuizStatus(id);
      const s = res.data;
      const opts = s.options || [];
      const detailDiv = document.getElementById('quiz-detail');
      if (!detailDiv) return;
      detailDiv.style.display = 'block';
      _chosenOption = null;

      let html = `<div style="background:#f8faff;border:1px solid var(--border);border-radius:8px;padding:1rem">
        <h4 style="margin-bottom:0.3rem">⚡ ${escHtml(s.title)}</h4>
        <div style="font-size:0.85rem;color:var(--text-light);margin-bottom:0.5rem">
          状态: <span class="badge ${s.status==='ACTIVE'?'badge-active':s.status==='PENDING'?'badge badge-active':'badge-closed'}">
            ${s.status==='ACTIVE'?'🔥 进行中 ('+s.submittedCount+'/3 已抢)':s.status==='PENDING'?'📋 待开始':'✅ 已结束'}
          </span>
          | 抢到名额: ${s.submittedCount}/3 | 答对: ${s.correctCount}
        </div>`;

      // 选项列表（抢到名额后才会显示选项）
      if (!isTeacher) {
        if (s.status === 'ACTIVE') {
          // 学生：先查自己是否已抢到
          try {
            const myRes = await API.getQuizRank(id);
            // 尝试抢名额
            html += '<div id="grab-section">';
            html += `<button class="btn btn-warning btn-lg" style="font-size:1.2rem;padding:0.8rem 2rem" onclick="grabQuiz(${id})">
              🏃 抢答！</button>`;
            html += '<p style="font-size:0.8rem;color:var(--text-light);margin-top:0.3rem">仅前 <strong>3</strong> 位同学可获得答题资格</p>';
            html += '</div>';
            html += '<div id="grab-result" style="margin-top:0.5rem"></div>';
          } catch(e) { /* ignore */ }
        } else if (s.status === 'PENDING') {
          html += '<p style="color:var(--text-light)">等待教师开始抢答...</p>';
        }
      } else {
        // 教师：显示选项 + 正确答案
        opts.forEach((o, i) => {
          const letter = String.fromCharCode(65 + i);
          const isCorrect = letter === s.correctOption;
          html += `<div style="padding:0.4rem 0;font-size:0.9rem;${isCorrect ? 'color:var(--success);font-weight:600' : ''}">
            ${isCorrect ? '✅ ' : ''}${escHtml(o)}
          </div>`;
        });
        html += `<p style="font-size:0.8rem;color:var(--success);margin-top:0.3rem">✅ 正确答案: ${s.correctOption}</p>`;
      }

      html += `<div style="margin-top:0.5rem"><button class="btn btn-outline btn-sm" onclick="document.getElementById('quiz-detail').style.display='none'">收起</button></div>`;
      html += '</div>';

      detailDiv.innerHTML = html;
      detailDiv.scrollIntoView({ behavior: 'smooth' });
    } catch (e) { alert(e.message); }
  };

  // ============================================================
  // 抢名额
  // ============================================================
  window.grabQuiz = async function(questionId) {
    const btn = document.querySelector('#grab-section .btn');
    if (btn) btn.disabled = true;
    try {
      const res = await API.grabQuiz({ questionId, studentId: user.id, chosenOption: '' });
      const data = res.data;
      const resultDiv = document.getElementById('grab-result');
      if (data.success) {
        if (resultDiv) {
          resultDiv.innerHTML = `<div style="padding:0.8rem;background:#dcfce7;border:2px solid var(--success);border-radius:8px;text-align:center;font-weight:700;font-size:1.1rem">
            ✅ ${data.message}！请选择答案：</div>`;
        }
        // 显示选项供答题
        const statusRes = await API.getQuizStatus(questionId);
        const opts = statusRes.data.options || [];
        let optHtml = '<hr style="margin:0.5rem 0"><p style="font-weight:600;margin-bottom:0.3rem">选择答案：</p>';
        opts.forEach((o, i) => {
          const letter = String.fromCharCode(65 + i);
          optHtml += `<div class="option-card" onclick="selectQuizOpt(this,'${letter}')">
            <span class="option-radio"></span><span>${escHtml(o)}</span>
          </div>`;
        });
        optHtml += `<button class="btn btn-success btn-sm" style="margin-top:0.5rem" id="btn-submit-quiz" disabled onclick="submitQuizAnswer(${questionId})">提交答案</button>`;
        optHtml += '<div id="quiz-feedback" style="margin-top:0.5rem"></div>';
        document.getElementById('grab-section').innerHTML = optHtml;
        loadQuiz();
      } else {
        if (resultDiv) {
          resultDiv.innerHTML = `<div style="padding:0.5rem;background:#fef2f2;border-radius:8px;font-weight:600">😅 ${data.message}</div>`;
        }
        if (btn) btn.disabled = false;
      }
    } catch (e) {
      const resultDiv = document.getElementById('grab-result');
      if (resultDiv) resultDiv.innerHTML = `<div style="padding:0.5rem;background:#fef2f2;border-radius:8px">${e.message}</div>`;
      if (btn) btn.disabled = false;
    }
  };

  window.selectQuizOpt = function(el, letter) {
    document.querySelectorAll('.option-card').forEach(c => c.classList.remove('selected'));
    el.classList.add('selected');
    _chosenOption = letter;
    const btn = document.getElementById('btn-submit-quiz');
    if (btn) btn.disabled = false;
  };

  window.submitQuizAnswer = async function(questionId) {
    if (!_chosenOption) return;
    const btn = document.getElementById('btn-submit-quiz');
    if (btn) btn.disabled = true;
    try {
      const res = await API.submitQuiz({ questionId, studentId: user.id, chosenOption: _chosenOption });
      const data = res.data;
      let msg = data.correct ? '✅ 回答正确！' : '❌ 回答错误';
      if (data.rank) msg += ' 第' + data.rank + '名';
      const el = document.getElementById('quiz-feedback');
      if (el) {
        el.innerHTML = `<div style="padding:0.5rem;background:${data.correct?'#dcfce7':'#fef2f2'};border-radius:8px;font-weight:600">
          ${msg} (正确答案: ${data.correctOption})</div>`;
      }
      loadQuiz();
    } catch (e) {
      const el = document.getElementById('quiz-feedback');
      if (el) el.innerHTML = `<div style="padding:0.5rem;background:#fef2f2;border-radius:8px">${e.message}</div>`;
      if (btn) btn.disabled = false;
    }
  };

  // ============================================================
  // 排行榜
  // ============================================================
  window.showQuizRank = async function(id) {
    try {
      const res = await API.getQuizRank(id);
      const r = res.data;
      const detailDiv = document.getElementById('quiz-detail');
      if (!detailDiv) return;
      detailDiv.style.display = 'block';

      let html = `<div style="background:#fffbf0;border:1px solid #fde68a;border-radius:8px;padding:1rem">
        <h4 style="margin-bottom:0.5rem">🏆 抢答排行榜 - ${escHtml(r.title)}</h4>`;

      if (!r.rankList || r.rankList.length === 0) {
        html += '<p style="color:var(--text-light)">暂无排名数据，等待学生抢答...</p>';
      } else {
        html += '<table style="width:100%;border-collapse:collapse">';
        html += '<tr style="border-bottom:2px solid var(--border)"><th style="text-align:left;padding:0.4rem">抢答顺序</th><th style="text-align:left;padding:0.4rem">姓名</th><th style="text-align:left;padding:0.4rem">抢答时间</th></tr>';
        r.rankList.forEach(item => {
          const medal = item.rank === 1 ? '🥇' : item.rank === 2 ? '🥈' : '🥉';
          const time = item.submitTime ? new Date(item.submitTime).toLocaleTimeString() : '-';
          html += `<tr style="border-bottom:1px solid var(--border)">
            <td style="padding:0.5rem 0.4rem;font-weight:700">${medal} ${item.rank}</td>
            <td style="padding:0.5rem 0.4rem">${escHtml(item.studentName)}</td>
            <td style="padding:0.5rem 0.4rem;color:var(--text-light)">${time}</td>
          </tr>`;
        });
        html += '</table>';
      }

      html += `<button class="btn btn-outline btn-sm" style="margin-top:0.5rem" onclick="document.getElementById('quiz-detail').style.display='none'">关闭</button>`;
      html += '</div>';

      detailDiv.innerHTML = html;
      detailDiv.scrollIntoView({ behavior: 'smooth' });
    } catch (e) { alert('排行榜查询失败: ' + e.message); }
  };

  // ============================================================
  // 挂载 + 加载
  // ============================================================
  window.loadQuiz = loadQuiz;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadQuiz);
  } else {
    setTimeout(loadQuiz, 500);
  }
})();
