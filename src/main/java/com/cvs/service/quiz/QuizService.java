package com.cvs.service.quiz;

import com.cvs.dto.quiz.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 抢答功能 Service 接口
 */
public interface QuizService {

    /**
     * 创建抢答题目
     *
     * @param request 创建请求
     * @return 题目状态
     */
    @Transactional
    QuizStatusVO createQuestion(QuizCreateRequest request);

    /**
     * 开始抢答（将题目状态改为进行中，记录开始时间到 Redis）
     *
     * @param questionId 题目ID
     * @return 题目状态
     */
    @Transactional
    QuizStatusVO startQuiz(Long questionId);

    /**
     * 提交答案
     * <p>
     * 核心逻辑：
     * 1. 校验题目状态（必须是 ACTIVE）
     * 2. Redis 分布式锁防止重复提交 (key=quiz:lock:questionId:studentId, 过期5秒)
     * 3. 判断选项正误
     * 4. 正确则 ZADD 到 Redis ZSET (key=quiz:rank:questionId, score=毫秒级时间戳)
     * 5. 返回结果 VO（是否正确、排名、正确答案）
     *
     * @param request 提交请求
     * @return 提交结果
     */
    @Transactional
    QuizResultVO submitAnswer(QuizSubmitRequest request);

    /**
     * 查询题目状态
     *
     * @param questionId 题目ID
     * @return 题目状态
     */
    QuizStatusVO getStatus(Long questionId);

    /**
     * 获取前3名排行榜
     * <p>
     * 优先从 Redis ZSET 获取，若 Redis 无数据则回查数据库
     *
     * @param questionId 题目ID
     * @return 排行榜列表
     */
    List<QuizRankVO> getTopRank(Long questionId);
}
