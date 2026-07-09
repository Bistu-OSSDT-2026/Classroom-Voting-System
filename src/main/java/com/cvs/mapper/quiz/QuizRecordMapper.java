package com.cvs.mapper.quiz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvs.model.quiz.QuizRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 抢答记录 Mapper
 * <p>
 * 自定义方法：
 * 1. 按题目ID查询答对者按时间升序排名（关联学生表查出姓名）
 * 2. 统计某学生是否已提交过某题
 */
@Mapper
public interface QuizRecordMapper extends BaseMapper<QuizRecord> {

    /**
     * 查询某题答对者的排名列表（按提交时间升序）
     * 关联 quiz_student 表查出学生姓名
     *
     * @param questionId 题目ID
     * @return 排名列表，每项包含 student_id, real_name, submit_time, rank
     */
    @Select("SELECT r.student_id, s.real_name, r.submit_time, " +
            "ROW_NUMBER() OVER (ORDER BY r.submit_time ASC) AS rank " +
            "FROM quiz_record r " +
            "LEFT JOIN quiz_student s ON r.student_id = s.id " +
            "WHERE r.question_id = #{questionId} AND r.is_correct = TRUE " +
            "ORDER BY r.submit_time ASC")
    List<Map<String, Object>> selectRankByQuestionId(@Param("questionId") Long questionId);

    /**
     * 统计某学生是否已提交过某题
     *
     * @param questionId 题目ID
     * @param studentId  学生ID
     * @return 提交记录数（0=未提交, >0=已提交）
     */
    @Select("SELECT COUNT(1) FROM quiz_record " +
            "WHERE question_id = #{questionId} AND student_id = #{studentId}")
    int countByQuestionIdAndStudentId(@Param("questionId") Long questionId,
                                      @Param("studentId") Long studentId);
}
