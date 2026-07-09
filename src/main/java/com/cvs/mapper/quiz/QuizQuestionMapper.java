package com.cvs.mapper.quiz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvs.model.quiz.QuizQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抢答题目 Mapper
 */
@Mapper
public interface QuizQuestionMapper extends BaseMapper<QuizQuestion> {
}
