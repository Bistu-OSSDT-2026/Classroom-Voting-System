package com.cvs.controller.quiz;

import com.cvs.dto.ApiResponse;
import com.cvs.dto.quiz.*;
import com.cvs.model.User;
import com.cvs.model.quiz.QuizQuestion;
import com.cvs.service.UserService;
import com.cvs.service.quiz.QuizService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课堂抢答 REST API
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ApiResponse<QuizQuestion> create(@Valid @RequestBody QuizCreateRequest req) {
        try {
            User teacher = userService.findById(req.getTeacherId());
            return ApiResponse.success("抢答题目创建成功", quizService.createQuestion(req, teacher));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/start/{id}")
    public ApiResponse<Void> start(@PathVariable Long id, @RequestParam Long teacherId) {
        try {
            quizService.startQuestion(id, userService.findById(teacherId));
            return ApiResponse.success("抢答已开始，学生可抢名额", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/grab")
    public ApiResponse<QuizGrabVO> grab(@RequestBody QuizSubmitRequest req) {
        try {
            User student = userService.findById(req.getStudentId());
            QuizGrabVO vo = quizService.grabQuestion(req.getQuestionId(), student);
            return ApiResponse.success(vo.getMessage(), vo);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/submit")
    public ApiResponse<QuizResultVO> submit(@Valid @RequestBody QuizSubmitRequest req) {
        try {
            User student = userService.findById(req.getStudentId());
            QuizResultVO result = quizService.submitAnswer(req, student);
            String msg = result.isCorrect()
                    ? (result.getRank() != null ? "回答正确！第" + result.getRank() + "名" : "回答正确")
                    : "回答错误";
            return ApiResponse.success(msg, result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    public ApiResponse<QuizStatusVO> status(@PathVariable Long id) {
        try {
            return ApiResponse.success(quizService.getQuestionStatus(id));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/rank/{id}")
    public ApiResponse<QuizRankVO> rank(@PathVariable Long id) {
        try {
            return ApiResponse.success(quizService.getRanking(id));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/by-course/{courseId}")
    public ApiResponse<List<QuizQuestion>> byCourse(@PathVariable Long courseId) {
        try {
            return ApiResponse.success(quizService.getCourseQuestions(courseId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
