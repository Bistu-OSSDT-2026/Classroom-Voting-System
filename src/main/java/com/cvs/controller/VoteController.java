package com.cvs.controller;

import com.cvs.dto.*;
import com.cvs.model.User;
import com.cvs.service.UserService;
import com.cvs.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vote-sessions")
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;

    public VoteController(VoteService voteService, UserService userService) {
        this.voteService = voteService;
        this.userService = userService;
    }

    /**
     * 创建投票（教师）
     */
    @PostMapping
    public ApiResponse<VoteSessionVO> createVote(
            @Valid @RequestBody CreateVoteRequest request,
            @RequestParam Long teacherId) {
        try {
            User teacher = userService.findById(teacherId);
            VoteSessionVO vo = voteService.createVote(request, teacher);
            return ApiResponse.success("投票创建成功", vo);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 获取投票详情（可选传入 studentId 判断学生是否已投票）
     */
    @GetMapping("/{sessionId}")
    public ApiResponse<VoteSessionVO> getVoteDetail(
            @PathVariable Long sessionId,
            @RequestParam(required = false) Long studentId) {
        try {
            VoteSessionVO vo = voteService.getVoteDetail(sessionId, studentId);
            return ApiResponse.success(vo);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 提交投票（学生），返回投票结果（是否正确、正确答案）
     */
    @PostMapping("/{sessionId}/vote")
    public ApiResponse<CastVoteResultVO> castVote(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Long> body) {
        try {
            Long studentId = body.get("studentId");
            Long optionId = body.get("optionId");

            User student = userService.findById(studentId);
            CastVoteResultVO result = voteService.castVote(sessionId, optionId, student);
            return ApiResponse.success(result.isCorrect() ? "回答正确！" : "回答错误", result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 关闭投票（教师）
     */
    @PutMapping("/{sessionId}/close")
    public ApiResponse<Void> closeVote(
            @PathVariable Long sessionId,
            @RequestParam Long teacherId) {
        try {
            User teacher = userService.findById(teacherId);
            voteService.closeVote(sessionId, teacher);
            return ApiResponse.success("投票已关闭", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 获取投票学生参与情况（教师用）
     */
    @GetMapping("/{sessionId}/students")
    public ApiResponse<VoteStudentVO> getVoteStudents(@PathVariable Long sessionId) {
        try {
            VoteStudentVO vo = voteService.getVoteStudents(sessionId);
            return ApiResponse.success(vo);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 获取课程下所有投票列表
     */
    @GetMapping("/by-course/{courseId}")
    public ApiResponse<List<VoteSessionVO>> getCourseVotes(@PathVariable Long courseId) {
        try {
            List<VoteSessionVO> votes = voteService.getCourseVotes(courseId);
            return ApiResponse.success(votes);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 获取投票的学生投票明细（仅教师可查看）
     */
    @GetMapping("/{sessionId}/records")
    public ApiResponse<List<VoteRecordVO>> getVoteRecords(
            @PathVariable Long sessionId,
            @RequestParam Long teacherId) {
        try {
            User teacher = userService.findById(teacherId);
            List<VoteRecordVO> records = voteService.getVoteRecords(sessionId, teacher);
            return ApiResponse.success(records);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
