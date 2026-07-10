package com.cvs.service.quiz;

import com.cvs.dto.quiz.*;
import com.cvs.model.User;
import com.cvs.model.quiz.QuizQuestion;
import com.cvs.model.quiz.QuizRecord;
import com.cvs.repository.quiz.QuizQuestionRepository;
import com.cvs.repository.quiz.QuizRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课堂抢答 业务逻辑
 * 流程：抢名额（前3人）→ 提交答案
 * 使用 JPA + 数据库唯一约束防重复，无需 Redis
 */
@Service
public class QuizService {

    private final QuizQuestionRepository questionRepository;
    private final QuizRecordRepository recordRepository;
    private final ObjectMapper objectMapper;

    /** 最大抢答名额 */
    private static final int MAX_GRABBERS = 3;

    public QuizService(QuizQuestionRepository questionRepository,
                       QuizRecordRepository recordRepository,
                       ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.recordRepository = recordRepository;
        this.objectMapper = objectMapper;
    }

    /** 1. 创建抢答题目 */
    @Transactional
    public QuizQuestion createQuestion(QuizCreateRequest request, User teacher) {
        if (teacher.getRole() != User.Role.TEACHER) {
            throw new RuntimeException("仅教师可创建抢答题目");
        }
        String optionsJson;
        try {
            optionsJson = objectMapper.writeValueAsString(request.getOptions());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("选项序列化失败", e);
        }
        QuizQuestion q = new QuizQuestion();
        q.setTitle(request.getTitle());
        q.setOptions(optionsJson);
        q.setCorrectOption(request.getCorrectOption().toUpperCase());
        q.setStatus(QuizQuestion.STATUS_PENDING);
        q.setCourseId(request.getCourseId());
        q.setTeacherId(teacher.getId());
        return questionRepository.save(q);
    }

    /** 2. 开始抢答 */
    @Transactional
    public void startQuestion(Long questionId, User teacher) {
        QuizQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        if (!q.getTeacherId().equals(teacher.getId())) {
            throw new RuntimeException("无权操作此题目");
        }
        if (!QuizQuestion.STATUS_PENDING.equals(q.getStatus())) {
            throw new RuntimeException("仅待开始的题目可开始抢答");
        }
        q.setStatus(QuizQuestion.STATUS_ACTIVE);
        q.setStartedAt(LocalDateTime.now());
        questionRepository.save(q);
    }

    /** 3. 抢名额（仅前3人可抢到） */
    @Transactional
    public QuizGrabVO grabQuestion(Long questionId, User student) {
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("仅学生可抢答");
        }

        QuizQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        if (!QuizQuestion.STATUS_ACTIVE.equals(q.getStatus())) {
            throw new RuntimeException("抢答未开始或已结束");
        }

        // 是否已抢过
        if (recordRepository.countByQuestionIdAndStudentId(questionId, student.getId()) > 0) {
            throw new RuntimeException("你已经抢过了");
        }

        // 检查是否还有名额
        long grabbed = recordRepository.countGrabbersByQuestionId(questionId);
        if (grabbed >= MAX_GRABBERS) {
            return new QuizGrabVO(false, 0, "名额已满，下次手速快点！");
        }

        // 分配抢答顺序
        int grabOrder = (int) grabbed + 1;

        QuizRecord rec = new QuizRecord();
        rec.setQuestionId(questionId);
        rec.setStudentId(student.getId());
        rec.setGrabOrder(grabOrder);
        rec.setGrabTime(LocalDateTime.now());
        recordRepository.save(rec);

        return new QuizGrabVO(true, grabOrder, "抢到了！你是第 " + grabOrder + " 个");
    }

    /** 4. 提交答案（仅已抢到名额者可提交） */
    @Transactional
    public QuizResultVO submitAnswer(QuizSubmitRequest request, User student) {
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("仅学生可提交答案");
        }

        QuizQuestion q = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        if (!QuizQuestion.STATUS_ACTIVE.equals(q.getStatus())) {
            throw new RuntimeException("抢答未开始或已结束");
        }

        // 查找抢答记录
        QuizRecord rec = recordRepository
                .findByQuestionIdAndStudentId(q.getId(), student.getId())
                .orElseThrow(() -> new RuntimeException("你没有抢到答题机会"));

        if (rec.getGrabOrder() == null) {
            throw new RuntimeException("你没有抢到答题机会");
        }
        if (rec.getChosenOption() != null) {
            throw new RuntimeException("你已经提交过答案了");
        }

        // 判断正误
        String chosen = request.getChosenOption().toUpperCase();
        boolean correct = q.getCorrectOption().equals(chosen);

        // 更新记录
        rec.setChosenOption(chosen);
        rec.setIsCorrect(correct);
        rec.setSubmitTime(LocalDateTime.now());
        recordRepository.save(rec);

        // 排名 = 抢答顺序
        Integer rank = correct ? rec.getGrabOrder() : null;

        return new QuizResultVO(correct, rank, q.getCorrectOption());
    }

    /** 5. 查询题目状态 */
    public QuizStatusVO getQuestionStatus(Long questionId) {
        QuizQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        QuizStatusVO vo = new QuizStatusVO();
        vo.setId(q.getId());
        vo.setTitle(q.getTitle());
        vo.setOptions(parseOptions(q.getOptions()));
        vo.setStatus(q.getStatus());
        vo.setStartedAt(q.getStartedAt());
        vo.setSubmittedCount((int) recordRepository.countGrabbersByQuestionId(questionId));
        vo.setCorrectCount((int) recordRepository.countByQuestionIdAndIsCorrectTrue(questionId));
        vo.setCorrectOption(q.getCorrectOption());
        return vo;
    }

    /** 6. 获取排行榜（按抢答顺序排列，仅显示已提交答案的） */
    public QuizRankVO getRanking(Long questionId) {
        QuizQuestion q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        List<QuizRecordRepository.GrabRankProjection> dbRank =
                recordRepository.findGrabRankByQuestionId(questionId);

        List<QuizRankVO.RankItem> rankList = new ArrayList<>();
        for (QuizRecordRepository.GrabRankProjection r : dbRank) {
            QuizRankVO.RankItem item = new QuizRankVO.RankItem();
            item.setRank(r.getGrabOrder());
            item.setStudentName(r.getStudentName());
            item.setSubmitTime(r.getGrabTime());
            rankList.add(item);
        }

        QuizRankVO vo = new QuizRankVO();
        vo.setQuestionId(questionId);
        vo.setTitle(q.getTitle());
        vo.setRankList(rankList);
        return vo;
    }

    /** 获取某课程的所有题目 */
    public List<QuizQuestion> getCourseQuestions(Long courseId) {
        return questionRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    @SuppressWarnings("unchecked")
    private List<String> parseOptions(String json) {
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
