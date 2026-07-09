package com.cvs.repository.quiz;

import com.cvs.model.quiz.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCourseIdOrderByCreatedAtDesc(Long courseId);
}
