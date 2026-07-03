/**
 * 单元测试 - Classroom Vote System
 */
import { describe, it, expect, beforeEach, vi } from "vitest";
import { loadQuestion, saveQuestion, initApp } from "../app.js";

// Mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: vi.fn((key) => store[key] ?? null),
    setItem: vi.fn((key, value) => {
      store[key] = value;
    }),
    removeItem: vi.fn((key) => {
      delete store[key];
    }),
    clear: vi.fn(() => {
      store = {};
    }),
  };
})();

Object.defineProperty(window, "localStorage", {
  value: localStorageMock,
  writable: true,
});

// Mock DOM elements needed by app
function setupDOM() {
  document.body.innerHTML = `
    <div id="app">
      <h2 id="question-title"></h2>
      <div id="options-container"></div>
      <div id="result-area" class="hidden">
        <div id="result-chart"></div>
        <p id="total-votes"></p>
      </div>
      <button id="btn-vote" disabled></button>
      <button id="btn-reset"></button>
      <p id="message"></p>
    </div>
  `;
}

describe("loadQuestion", () => {
  beforeEach(() => {
    localStorageMock.clear();
  });

  it("should return default question when localStorage is empty", () => {
    const question = loadQuestion();
    expect(question.id).toBe("q1");
    expect(question.options).toHaveLength(4);
    expect(question.options[0].votes).toBe(0);
  });

  it("should return saved question from localStorage", () => {
    const custom = {
      id: "q1",
      title: "测试题目",
      options: [
        { id: "opt1", text: "A", votes: 5 },
        { id: "opt2", text: "B", votes: 3 },
      ],
    };
    localStorageMock.setItem("cvs_question", JSON.stringify(custom));

    const question = loadQuestion();
    expect(question.title).toBe("测试题目");
    expect(question.options[0].votes).toBe(5);
  });

  it("should fallback to default when localStorage has corrupt data", () => {
    localStorageMock.setItem("cvs_question", "{invalid json");

    const question = loadQuestion();
    expect(question.id).toBe("q1");
  });
});

describe("saveQuestion", () => {
  beforeEach(() => {
    localStorageMock.clear();
  });

  it("should save question to localStorage", () => {
    const question = {
      id: "q1",
      title: "测试",
      options: [{ id: "opt1", text: "A", votes: 10 }],
    };
    saveQuestion(question);

    expect(localStorageMock.setItem).toHaveBeenCalledWith(
      "cvs_question",
      JSON.stringify(question)
    );
  });
});

describe("initApp", () => {
  beforeEach(() => {
    localStorageMock.clear();
    setupDOM();
  });

  it("should render question title on init", () => {
    initApp();

    const title = document.getElementById("question-title");
    expect(title.textContent).toBeTruthy();
  });

  it("should render option items on init", () => {
    initApp();

    const options = document.querySelectorAll(".option-item");
    expect(options.length).toBeGreaterThan(0);
  });

  it("should have vote button disabled initially", () => {
    initApp();

    const btn = document.getElementById("btn-vote");
    expect(btn.disabled).toBe(true);
  });
});
