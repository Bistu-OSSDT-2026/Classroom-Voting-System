package com.cvs.repository.quiz;

import com.cvs.model.quiz.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {

    /** 按抢答顺序查询已抢到名额的学生（关联用户表查出姓名） */
    @Query("SELECT r.studentId AS studentId, u.realName AS studentName, r.grabOrder AS grabOrder, r.grabTime AS grabTime " +
           "FROM QuizRecord r JOIN User u ON u.id = r.studentId " +
           "WHERE r.questionId = :questionId AND r.grabOrder IS NOT NULL " +
           "ORDER BY r.grabOrder ASC")
    List<GrabRankProjection> findGrabRankByQuestionId(@Param("questionId") Long questionId);

    /** 统计某学生是否已抢过某题 */
    long countByQuestionIdAndStudentId(Long questionId, Long studentId);

    /** 统计已抢到名额的人数 */
    @Query("SELECT COUNT(r) FROM QuizRecord r WHERE r.questionId = :questionId AND r.grabOrder IS NOT NULL")
    long countGrabbersByQuestionId(@Param("questionId") Long questionId);

    /** 统计某题的总提交人数 */
    long countByQuestionId(Long questionId);

    /** 统计某题的答对人数 */
    long countByQuestionIdAndIsCorrectTrue(Long questionId);

    /** 查找某学生的抢答记录（判断是否已抢到） */
    Optional<QuizRecord> findByQuestionIdAndStudentId(Long questionId, Long studentId);

    /** 抢答排名投影接口 */
    interface GrabRankProjection {
        Long getStudentId();
        String getStudentName();
        Integer getGrabOrder();
        java.time.LocalDateTime getGrabTime();
    }
}
