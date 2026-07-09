package com.cvs.service;

import com.cvs.dto.CastVoteResultVO;
import com.cvs.dto.CreateVoteRequest;
import com.cvs.dto.VoteSessionVO;
import com.cvs.model.*;
import com.cvs.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final VoteSessionRepository voteSessionRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final CourseService courseService;
    private final KnowledgePointService knowledgePointService;

    public VoteService(VoteSessionRepository voteSessionRepository,
                       VoteOptionRepository voteOptionRepository,
                       VoteRecordRepository voteRecordRepository,
                       CourseService courseService,
                       KnowledgePointService knowledgePointService) {
        this.voteSessionRepository = voteSessionRepository;
        this.voteOptionRepository = voteOptionRepository;
        this.voteRecordRepository = voteRecordRepository;
        this.courseService = courseService;
        this.knowledgePointService = knowledgePointService;
    }

    /**
     * 创建投票（教师）
     */
    @Transactional
    public VoteSessionVO createVote(CreateVoteRequest request, User teacher) {
        Course course = courseService.findById(request.getCourseId());
        KnowledgePoint kp = knowledgePointService.findById(request.getKnowledgePointId());

        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("无权操作");
        }

        VoteSession session = new VoteSession(request.getTitle(), course, kp, teacher);
        session = voteSessionRepository.save(session);

        for (CreateVoteRequest.OptionItem item : request.getOptions()) {
            VoteOption option = new VoteOption(item.getText(), item.getIsCorrect(), session);
            voteOptionRepository.save(option);
        }

        List<VoteOption> options = voteOptionRepository.findByVoteSession(session);
        return VoteSessionVO.fromVoteSession(session, options, 0);
    }

    /**
     * 获取投票详情（含选项和票数统计）
     * @param sessionId 投票ID
     * @param studentId 可选，学生ID；用于判断该学生是否已投票、是否隐藏票数
     */
    public VoteSessionVO getVoteDetail(Long sessionId, Long studentId) {
        VoteSession session = voteSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("投票不存在"));

        List<VoteOption> options = voteOptionRepository.findByVoteSession(session);
        List<VoteRecord> records = voteRecordRepository.findByVoteSession(session);

        // 统计每个选项的票数
        Map<Long, Long> optionVoteCount = records.stream()
                .collect(Collectors.groupingBy(r -> r.getOption().getId(), Collectors.counting()));

        VoteSessionVO vo = VoteSessionVO.fromVoteSession(session, options, records.size());

        for (VoteSessionVO.OptionVO optVO : vo.getOptions()) {
            optVO.setVoteCount(optionVoteCount.getOrDefault(optVO.getId(), 0L));
        }

        // 计算正确率
        long correctCount = records.stream()
                .filter(r -> r.getOption().getIsCorrect())
                .count();
        double correctRate = records.isEmpty() ? 0 :
                Math.round((double) correctCount / records.size() * 10000.0) / 100.0;
        vo.setCorrectRate(correctRate);

        // 学生查看时：判断是否已投票，未投票则隐藏票数
        if (studentId != null) {
            boolean studentHasVoted = records.stream()
                    .anyMatch(r -> r.getStudent().getId().equals(studentId));

            if (!studentHasVoted) {
                // 学生未投票：隐藏票数和正确答案
                vo.setHasVoted(false);
                for (VoteSessionVO.OptionVO optVO : vo.getOptions()) {
                    optVO.setVoteCount(0);
                    optVO.setIsCorrect(null);
                }
                vo.setTotalVotes(0);
            } else {
                // 学生已投票：标记并记录所投选项
                vo.setHasVoted(true);
                VoteRecord myRecord = records.stream()
                        .filter(r -> r.getStudent().getId().equals(studentId))
                        .findFirst()
                        .orElse(null);
                if (myRecord != null) {
                    vo.setStudentVoteOptionId(myRecord.getOption().getId());
                }
            }
        }

        return vo;
    }

    /**
     * 提交投票（学生），返回投票结果信息
     */
    @Transactional
    public CastVoteResultVO castVote(Long sessionId, Long optionId, User student) {
        VoteSession session = voteSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("投票不存在"));

        if (session.getStatus() == VoteSession.Status.CLOSED) {
            throw new RuntimeException("投票已关闭");
        }

        if (!student.getRole().equals(User.Role.STUDENT)) {
            throw new RuntimeException("仅学生可投票");
        }

        if (voteRecordRepository.existsByVoteSessionAndStudent(session, student)) {
            throw new RuntimeException("你已投过票");
        }

        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("选项不存在"));

        if (!option.getVoteSession().getId().equals(sessionId)) {
            throw new RuntimeException("选项不属于该投票");
        }

        VoteRecord record = new VoteRecord(session, option, student);
        voteRecordRepository.save(record);

        // 查找正确答案
        List<VoteOption> options = voteOptionRepository.findByVoteSession(session);
        VoteOption correctOption = options.stream()
                .filter(VoteOption::getIsCorrect)
                .findFirst()
                .orElse(null);

        CastVoteResultVO result = new CastVoteResultVO();
        result.setCorrect(option.getIsCorrect());
        result.setChosenOptionId(optionId);
        if (correctOption != null) {
            result.setCorrectOptionId(correctOption.getId());
            result.setCorrectOptionText(correctOption.getText());
        }
        return result;
    }

    /**
     * 关闭投票（教师）
     */
    @Transactional
    public void closeVote(Long sessionId, User teacher) {
        VoteSession session = voteSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("投票不存在"));

        if (!session.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("无权操作");
        }

        session.setStatus(VoteSession.Status.CLOSED);
        voteSessionRepository.save(session);
    }

    /**
     * 获取课程下的所有投票
     */
    public List<VoteSessionVO> getCourseVotes(Long courseId) {
        List<VoteSession> sessions = voteSessionRepository.findByCourseId(courseId);
        return sessions.stream().map(s -> {
            List<VoteOption> options = voteOptionRepository.findByVoteSession(s);
            List<VoteRecord> records = voteRecordRepository.findByVoteSession(s);

            // 计算正确率
            long correctCount = records.stream()
                    .filter(r -> r.getOption().getIsCorrect())
                    .count();
            double correctRate = records.isEmpty() ? 0 :
                    Math.round((double) correctCount / records.size() * 10000.0) / 100.0;

            VoteSessionVO vo = VoteSessionVO.fromVoteSession(s, options, records.size());
            vo.setCorrectRate(correctRate);
            return vo;
        }).collect(Collectors.toList());
    }
}
