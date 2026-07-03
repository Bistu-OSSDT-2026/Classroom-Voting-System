/**
 * Classroom Vote System - 核心投票逻辑
 *
 * 功能：
 * - 展示投票题目与选项
 * - 用户选择并提交投票
 * - 实时显示投票结果
 * - localStorage 持久化投票数据
 */

// ===== 默认投票题目 =====
const DEFAULT_QUESTION = {
  id: "q1",
  title: "你最喜欢哪种编程语言？",
  options: [
    { id: "opt1", text: "JavaScript / TypeScript", votes: 0 },
    { id: "opt2", text: "Python", votes: 0 },
    { id: "opt3", text: "Java", votes: 0 },
    { id: "opt4", text: "Go / Rust", votes: 0 },
  ],
};

const STORAGE_KEY = "cvs_question";

// ===== 状态管理 =====
const state = {
  question: null,
  selectedOptionId: null,
  hasVoted: false,
};

// ===== DOM 引用 =====
function getElements() {
  return {
    questionTitle: document.getElementById("question-title"),
    optionsContainer: document.getElementById("options-container"),
    resultArea: document.getElementById("result-area"),
    resultChart: document.getElementById("result-chart"),
    totalVotes: document.getElementById("total-votes"),
    btnVote: document.getElementById("btn-vote"),
    btnReset: document.getElementById("btn-reset"),
    message: document.getElementById("message"),
  };
}

// ===== 数据持久化 =====
export function loadQuestion() {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const parsed = JSON.parse(saved);
      if (parsed && parsed.id && Array.isArray(parsed.options)) {
        return parsed;
      }
    }
  } catch {
    // 数据损坏时回退到默认值
  }
  return structuredClone(DEFAULT_QUESTION);
}

export function saveQuestion(question) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(question));
  } catch {
    // 存储空间不足时静默失败
  }
}

// ===== 渲染 =====
function renderOptions(elements) {
  const { question } = state;
  elements.optionsContainer.innerHTML = "";

  question.options.forEach((opt) => {
    const div = document.createElement("div");
    div.className = "option-item";
    if (state.selectedOptionId === opt.id) {
      div.classList.add("selected");
    }

    div.innerHTML = `
      <span class="option-radio"></span>
      <span class="option-label">${escapeHtml(opt.text)}</span>
    `;

    div.addEventListener("click", () => selectOption(opt.id, elements));
    elements.optionsContainer.appendChild(div);
  });
}

function renderResults(elements) {
  const { question } = state;
  const totalVotes = question.options.reduce((sum, o) => sum + o.votes, 0);

  if (totalVotes > 0) {
    elements.resultArea.classList.remove("hidden");
  }

  elements.resultChart.innerHTML = question.options
    .map((opt) => {
      const percent = totalVotes > 0 ? Math.round((opt.votes / totalVotes) * 100) : 0;
      return `
        <div class="result-bar-wrapper">
          <div class="result-bar-header">
            <span>${escapeHtml(opt.text)}</span>
            <span>${opt.votes} 票 (${percent}%)</span>
          </div>
          <div class="result-bar-track">
            <div class="result-bar-fill" style="width: ${percent}%"></div>
          </div>
        </div>
      `;
    })
    .join("");

  elements.totalVotes.textContent = `总票数: ${totalVotes}`;
}

function updateUI(elements) {
  elements.questionTitle.textContent = state.question.title;
  renderOptions(elements);
  renderResults(elements);

  // 按钮状态
  elements.btnVote.disabled = !state.selectedOptionId || state.hasVoted;
  if (state.hasVoted) {
    elements.btnVote.textContent = "✅ 已投票";
  } else {
    elements.btnVote.textContent = "提交投票";
  }
}

// ===== 交互逻辑 =====
function selectOption(optionId, elements) {
  if (state.hasVoted) return;

  state.selectedOptionId = optionId;
  renderOptions(elements);
  elements.btnVote.disabled = false;
  clearMessage(elements);
}

function submitVote(elements) {
  if (!state.selectedOptionId || state.hasVoted) return;

  // 更新票数
  const option = state.question.options.find(
    (o) => o.id === state.selectedOptionId
  );
  if (option) {
    option.votes += 1;
  }

  state.hasVoted = true;
  saveQuestion(state.question);

  showMessage(elements, "✅ 投票成功！感谢你的参与", "success");
  updateUI(elements);
}

function resetVotes(elements) {
  state.question = structuredClone(DEFAULT_QUESTION);
  state.selectedOptionId = null;
  state.hasVoted = false;
  saveQuestion(state.question);

  showMessage(elements, "🔄 投票已重置，可以重新投票", "success");
  updateUI(elements);
}

// ===== 工具函数 =====
function escapeHtml(str) {
  const div = document.createElement("div");
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}

function showMessage(elements, text, type = "") {
  elements.message.textContent = text;
  elements.message.className = "message " + type;
}

function clearMessage(elements) {
  elements.message.textContent = "";
  elements.message.className = "message";
}

// ===== 事件绑定 =====
function bindEvents(elements) {
  elements.btnVote.addEventListener("click", () => submitVote(elements));
  elements.btnReset.addEventListener("click", () => resetVotes(elements));
}

// ===== 初始化 =====
export function initApp() {
  state.question = loadQuestion();
  const elements = getElements();

  bindEvents(elements);
  updateUI(elements);
}
