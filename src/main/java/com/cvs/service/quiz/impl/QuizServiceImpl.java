package com.cvs.service.quiz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cvs.dto.quiz.*;
import com.cvs.mapper.quiz.QuizQuestionMapper;
import com.cvs.mapper.quiz.QuizRecordMapper;
import com.cvs.mapper.quiz.QuizStudentMapper;
import com.cvs.model.quiz.QuizQuestion;
import com.cvs.model.quiz.QuizRecord;
import com.cvs.model.quiz.QuizStudent;
import com.cvs.service.quiz.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 抢答功能 Service 实现
 */
@Slf4j
@Service
public class QuizServiceImpl implements QuizService {

    public QuizServiceImpl(QuizQuestionMapper questionMapper,
                           QuizRecordMapper recordMapper,
                           QuizStudentMapper studentMapper) {
        this.questionMapper = questionMapper;
        this.recordMapper = recordMapper;
        this.studentMapper = studentMapper;
    }

    // ======================== Redis Key 常量 ========================

    /** 当前活跃题目ID */
    private static final String KEY_ACTIVE = "quiz:active";
    /** 题目信息缓存 */
    private static final String KEY_INFO = "quiz:info:";
    /** 排行榜 ZSET */
    private static final String KEY_RANK = "quiz:rank:";
    /** 分布式锁前缀 */
    private static final String KEY_LOCK = "quiz:lock:";

    // ======================== Mapper & Redis ========================

    private final QuizQuestionMapper questionMapper;
    private final QuizRecordMapper recordMapper;
    private final QuizStudentMapper studentMapper;

    /** Redis 操作（无 Redis 时可为 null，降级为数据库操作） */
    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    /** Redis 是否可用 */
    private boolean redisAvailable() {
        return stringRedisTemplate != null;
    }

    // ======================== 1. 创建题目 ========================

    @Override
    @Transactional
    public QuizStatusVO createQuestion(QuizCreateRequest request) {
        // 构建实体
        QuizQuestion question = QuizQuestion.builder()
                .title(request.getTitle())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer().toUpperCase())
                .status(QuizQuestion.STATUS_PENDING)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        questionMapper.insert(question);
        log.info("创建抢答题目成功, id={}, title={}", question.getId(), question.getTitle());

        return buildStatusVO(question, 0, 0);
    }

    // ======================== 2. 开始抢答 ========================

    @Override
    @Transactional
    public QuizStatusVO startQuiz(Long questionId) {
        // 查询题目
        QuizQuestion question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在, id=" + questionId);
        }
        if (!QuizQuestion.STATUS_PENDING.equals(question.getStatus())) {
            throw new RuntimeException("题目状态不允许开始抢答, 当前状态=" + question.getStatus());
        }

        // 更新状态为进行中
        question.setStatus(QuizQuestion.STATUS_ACTIVE);
        question.setStartedAt(LocalDateTime.now());
        questionMapper.updateById(question);

        // ---- Redis 缓存操作（Redis 可用时） ----
        if (redisAvailable()) {
            // 1. 设置当前活跃题目
            stringRedisTemplate.opsForValue().set(KEY_ACTIVE, String.valueOf(questionId), 1, TimeUnit.HOURS);

            // 2. 缓存题目基本信息（5分钟过期）
            String infoKey = KEY_INFO + questionId;
            Map<String, String> infoMap = buildInfoMap(question);
            stringRedisTemplate.opsForHash().putAll(infoKey, infoMap);
            stringRedisTemplate.expire(infoKey, 5, TimeUnit.MINUTES);
        }

        log.info("开始抢答, questionId={}, startedAt={}", questionId, question.getStartedAt());
        return buildStatusVO(question, 0, 0);
    }

    // ======================== 3. 提交答案（核心方法） ========================

    @Override
    @Transactional
    public QuizResultVO submitAnswer(QuizSubmitRequest request) {
        Long questionId = request.getQuestionId();
        Long studentId = request.getStudentId();
        String selectedAnswer = request.getSelectedAnswer().toUpperCase();

        // ---- 3.1 校验题目状态 ----
        QuizQuestion question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        if (!QuizQuestion.STATUS_ACTIVE.equals(question.getStatus())) {
            throw new RuntimeException("题目不在抢答进行中, 当前状态=" + question.getStatus());
        }

        // ---- 3.2 Redis 分布式锁防止重复提交（Redis 可用时） ----
        if (redisAvailable()) {
            String lockKey = KEY_LOCK + questionId + ":" + studentId;
            // Lua 脚本：SET NX + EX 原子操作
            String lockScript = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('expire', KEYS[1], ARGV[2]); return 1; " +
                    "else return 0; end";
            Boolean locked = stringRedisTemplate.execute(
                    new DefaultRedisScript<>(lockScript, Boolean.class),
                    Collections.singletonList(lockKey),
                    "locked", "5");

            if (Boolean.FALSE.equals(locked)) {
                throw new RuntimeException("请勿重复提交");
            }
        }

        try {
            // ---- 3.3 再次校验数据库防止重复（兜底） ----
            int existCount = recordMapper.countByQuestionIdAndStudentId(questionId, studentId);
            if (existCount > 0) {
                throw new RuntimeException("您已提交过答案，不可重复提交");
            }

            // ---- 3.4 判断正误 ----
            String correctAnswer = question.getCorrectAnswer();
            boolean isCorrect = correctAnswer.equals(selectedAnswer);

            // 当前毫秒级时间戳
            long nowMillis = System.currentTimeMillis();

            // ---- 3.5 插入数据库记录 ----
            QuizRecord record = QuizRecord.builder()
                    .questionId(questionId)
                    .studentId(studentId)
                    .selectedAnswer(selectedAnswer)
                    .isCorrect(isCorrect)
                    .submitTime(LocalDateTime.now())
                    .build();
            recordMapper.insert(record);

            // ---- 3.6 答对则写入 Redis ZSET 排名（Redis 不可用时回查数据库） ----
            Integer rank = null;
            int totalCorrect = 0;

            if (isCorrect) {
                if (redisAvailable()) {
                    String rankKey = KEY_RANK + questionId;
                    // ZADD 原子操作，score 使用毫秒级时间戳
                    stringRedisTemplate.opsForZSet().add(rankKey, String.valueOf(studentId), nowMillis);

                    // 查询当前排名（从1开始）
                    Long zRank = stringRedisTemplate.opsForZSet().rank(rankKey, String.valueOf(studentId));
                    rank = (zRank != null) ? zRank.intValue() + 1 : 1;

                    // 查询答对总人数
                    Long total = stringRedisTemplate.opsForZSet().zCard(rankKey);
                    totalCorrect = (total != null) ? total.intValue() : 1;
                } else {
                    // Redis 不可用，从数据库查询答对排名
                    List<Map<String, Object>> rankList = recordMapper.selectRankByQuestionId(questionId);
                    for (int i = 0; i < rankList.size(); i++) {
                        Long sid = ((Number) rankList.get(i).get("student_id")).longValue();
                        if (sid.equals(studentId)) {
                            rank = i + 1;
                            break;
                        }
                    }
                    totalCorrect = rankList.size();
                }
            } else {
                Long total = recordMapper.selectCount(
                        new LambdaQueryWrapper<QuizRecord>()
                                .eq(QuizRecord::getQuestionId, questionId)
                                .eq(QuizRecord::getIsCorrect, true));
                totalCorrect = (total != null) ? total.intValue() : 0;
            }

            log.info("提交答案, questionId={}, studentId={}, correct={}, rank={}",
                    questionId, studentId, isCorrect, rank);

            return QuizResultVO.builder()
                    .correct(isCorrect)
                    .rank(rank)
                    .correctAnswer(correctAnswer)
                    .totalCorrect(totalCorrect)
                    .build();

        } catch (RuntimeException e) {
            throw e;
        } finally {
            // 释放分布式锁
            if (redisAvailable()) {
                stringRedisTemplate.delete(KEY_LOCK + questionId + ":" + studentId);
            }
        }
    }

    // ======================== 4. 查询题目状态 ========================

    @Override
    public QuizStatusVO getStatus(Long questionId) {
        // 优先从 Redis 缓存获取
        if (redisAvailable()) {
            String infoKey = KEY_INFO + questionId;
            Map<Object, Object> cacheMap = stringRedisTemplate.opsForHash().entries(infoKey);

            if (!cacheMap.isEmpty()) {
                QuizStatusVO vo = new QuizStatusVO();
                vo.setId(Long.valueOf((String) cacheMap.get("id")));
                vo.setTitle((String) cacheMap.get("title"));
                vo.setOptionA((String) cacheMap.get("optionA"));
                vo.setOptionB((String) cacheMap.get("optionB"));
                vo.setOptionC((String) cacheMap.get("optionC"));
                vo.setOptionD((String) cacheMap.get("optionD"));
                vo.setStatus((String) cacheMap.get("status"));

                String startedAt = (String) cacheMap.get("startedAt");
                if (startedAt != null) {
                    vo.setStartedAt(LocalDateTime.parse(startedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }

                // 从 ZSET 获取统计
                String rankKey = KEY_RANK + questionId;
                Long correctCount = stringRedisTemplate.opsForZSet().zCard(rankKey);
                vo.setCorrectCount(correctCount != null ? correctCount.intValue() : 0);

                // 已提交人数从数据库查询（答对+答错）
                Long submitted = recordMapper.selectCount(
                        new LambdaQueryWrapper<QuizRecord>()
                                .eq(QuizRecord::getQuestionId, questionId));
                vo.setSubmittedCount(submitted != null ? submitted.intValue() : 0);

                return vo;
            }
        }

        // 缓存未命中或 Redis 不可用，查数据库
        QuizQuestion question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        long correctCount = recordMapper.selectCount(
                new LambdaQueryWrapper<QuizRecord>()
                        .eq(QuizRecord::getQuestionId, questionId)
                        .eq(QuizRecord::getIsCorrect, true));
        long submittedCount = recordMapper.selectCount(
                new LambdaQueryWrapper<QuizRecord>()
                        .eq(QuizRecord::getQuestionId, questionId));

        return buildStatusVO(question, (int) submittedCount, (int) correctCount);
    }

    // ======================== 5. 获取所有题目列表 ========================

    @Override
    public List<QuizStatusVO> listAll() {
        List<QuizQuestion> list = questionMapper.selectAllOrderByCreatedAtDesc();
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuizStatusVO> result = new ArrayList<>();
        for (QuizQuestion q : list) {
            long correctCount = recordMapper.selectCount(
                    new LambdaQueryWrapper<QuizRecord>()
                            .eq(QuizRecord::getQuestionId, q.getId())
                            .eq(QuizRecord::getIsCorrect, true));
            long submittedCount = recordMapper.selectCount(
                    new LambdaQueryWrapper<QuizRecord>()
                            .eq(QuizRecord::getQuestionId, q.getId()));
            result.add(buildStatusVO(q, (int) submittedCount, (int) correctCount));
        }
        return result;
    }

    // ======================== 6. 获取前3名排行榜 ========================

    @Override
    public List<QuizRankVO> getTopRank(Long questionId) {
        // ---- 优先从 Redis ZSET 获取（Redis 可用时） ----
        if (redisAvailable()) {
            String rankKey = KEY_RANK + questionId;
            Set<String> topMembers = stringRedisTemplate.opsForZSet()
                    .range(rankKey, 0, 2); // 前3名

            if (topMembers != null && !topMembers.isEmpty()) {
                List<QuizRankVO> rankList = new ArrayList<>();
                int rank = 1;
                for (String member : topMembers) {
                    Long studentId = Long.valueOf(member);
                    Double score = stringRedisTemplate.opsForZSet().score(rankKey, member);

                    // 查询学生姓名
                    QuizStudent student = studentMapper.selectById(studentId);
                    String studentName = (student != null) ? student.getRealName() : "未知";

                    QuizRankVO vo = QuizRankVO.builder()
                            .rank(rank++)
                            .studentName(studentName)
                            .submitTime(formatSubmitTime(score))
                            .build();
                    rankList.add(vo);
                }
                return rankList;
            }
        }

        // ---- Redis 无数据或不可用，回查数据库 ----
        List<Map<String, Object>> dbRankList = recordMapper.selectRankByQuestionId(questionId);
        if (dbRankList == null || dbRankList.isEmpty()) {
            return Collections.emptyList();
        }

        return dbRankList.stream()
                .limit(3)
                .map(row -> QuizRankVO.builder()
                        .rank(((Number) row.get("rank")).intValue())
                        .studentName((String) row.get("real_name"))
                        .submitTime(row.get("submit_time") != null ?
                                row.get("submit_time").toString() : "")
                        .build())
                .collect(Collectors.toList());
    }

    // ======================== 辅助方法 ========================

    /**
     * 构建题目信息 Map（用于 Redis Hash 缓存）
     */
    private Map<String, String> buildInfoMap(QuizQuestion question) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(question.getId()));
        map.put("title", question.getTitle());
        map.put("optionA", question.getOptionA());
        map.put("optionB", question.getOptionB());
        map.put("optionC", question.getOptionC() != null ? question.getOptionC() : "");
        map.put("optionD", question.getOptionD() != null ? question.getOptionD() : "");
        map.put("status", question.getStatus());
        map.put("startedAt", question.getStartedAt() != null ?
                question.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        return map;
    }

    /**
     * 构建状态 VO
     */
    private QuizStatusVO buildStatusVO(QuizQuestion question, int submittedCount, int correctCount) {
        return QuizStatusVO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .status(question.getStatus())
                .startedAt(question.getStartedAt())
                .submittedCount(submittedCount)
                .correctCount(correctCount)
                .build();
    }

    /**
     * 格式化毫秒级时间戳为字符串
     */
    private String formatSubmitTime(Double score) {
        if (score == null) return "";
        long millis = score.longValue();
        LocalDateTime dt = java.time.Instant.ofEpochMilli(millis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
