package com.cvs.mapper.quiz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvs.model.quiz.QuizStudent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper（抢答模块专用）
 */
@Mapper
public interface QuizStudentMapper extends BaseMapper<QuizStudent> {
}
