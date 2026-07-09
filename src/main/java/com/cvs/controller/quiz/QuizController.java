package com.cvs.controller.quiz;

import com.cvs.dto.quiz.*;
import com.cvs.service.quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课堂抢答 Controller
 * <p>
 * RESTful API:
 * - POST   /api/quiz/create      创建抢答题目
 * - POST   /api/quiz/start/{id}  开始抢答
 * - POST   /api/quiz/submit      提交答案
 * - GET    /api/quiz/status/{id} 查询题目状态
 * - GET    /api/quiz/rank/{id}   获取前3名排行榜
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "课堂抢答", description = "课堂抢答功能 RESTful API")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/create")
    @Operation(summary = "创建抢答题目", description = "教师创建一道抢答题目，初始状态为 PENDING")
    public Result<QuizStatusVO> createQuestion(
            @Valid @RequestBody QuizCreateRequest request) {
        try {
            QuizStatusVO vo = quizService.createQuestion(request);
            return Result.success("题目创建成功", vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/start/{id}")
    @Operation(summary = "开始抢答", description = "教师将题目状态改为进行中(ACTIVE)，并记录开始时间到Redis")
    public Result<QuizStatusVO> startQuiz(
            @Parameter(description = "题目ID", example = "1")
            @PathVariable Long id) {
        try {
            QuizStatusVO vo = quizService.startQuiz(id);
            return Result.success("抢答已开始", vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "提交答案", description = "学生提交答案，使用Redis分布式锁防重复，正确则写入ZSET排名")
    public Result<QuizResultVO> submitAnswer(
            @Valid @RequestBody QuizSubmitRequest request) {
        try {
            QuizResultVO vo = quizService.submitAnswer(request);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "查询题目状态", description = "查询题目当前状态、已提交人数、答对人数")
    public Result<QuizStatusVO> getStatus(
            @Parameter(description = "题目ID", example = "1")
            @PathVariable Long id) {
        try {
            QuizStatusVO vo = quizService.getStatus(id);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取题目列表", description = "获取所有抢答题目列表，按创建时间降序")
    public Result<List<QuizStatusVO>> listAll() {
        try {
            List<QuizStatusVO> list = quizService.listAll();
            return Result.success(list);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/rank/{id}")
    @Operation(summary = "获取前3名排行榜", description = "获取某题答对者按提交时间升序的前3名，优先从Redis ZSET获取")
    public Result<List<QuizRankVO>> getTopRank(
            @Parameter(description = "题目ID", example = "1")
            @PathVariable Long id) {
        try {
            List<QuizRankVO> rankList = quizService.getTopRank(id);
            return Result.success(rankList);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
