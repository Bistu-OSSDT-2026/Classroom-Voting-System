package com.cvs.mapper.quiz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvs.model.quiz.QuizQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 抢答题目 Mapper
 */
@Mapper
public interface QuizQuestionMapper extends BaseMapper<QuizQuestion> {

    /**
     * 查询所有题目，按创建时间降序
     */
    @Select("SELECT * FROM quiz_question ORDER BY created_at DESC")
    List<QuizQuestion> selectAllOrderByCreatedAtDesc();
}
