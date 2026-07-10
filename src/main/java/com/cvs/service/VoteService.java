package com.cvs.service;

import com.cvs.dto.CastVoteResultVO;
import com.cvs.dto.CreateVoteRequest;
import com.cvs.dto.VoteRecordVO;
import com.cvs.dto.VoteSessionVO;
import com.cvs.dto.VoteStudentVO;
import com.cvs.model.*;
import com.cvs.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        VoteSession session = new VoteSession(request.getTitle(), course, kp, teacher, request.isAnonymous());
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

        // 实名投票时收集每个选项对应的学生用户名
        Map<Long, List<String>> optionStudents = null;
        if (!session.isAnonymous()) {
            optionStudents = records.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getOption().getId(),
                            Collectors.mapping(r -> r.getStudent().getUsername(), Collectors.toList())));
        }

        VoteSessionVO vo = VoteSessionVO.fromVoteSession(session, options, records.size());

        for (VoteSessionVO.OptionVO optVO : vo.getOptions()) {
            optVO.setVoteCount(optionVoteCount.getOrDefault(optVO.getId(), 0L));
            if (optionStudents != null) {
                optVO.setStudents(optionStudents.getOrDefault(optVO.getId(), List.of()));
            }
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
     * 获取投票的学生参与情况（已投票/未投票）
     */
    public VoteStudentVO getVoteStudents(Long sessionId) {
        VoteSession session = voteSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("投票不存在"));

        // 获取课程下所有选课学生
        List<User> allStudents = courseService.getCourseStudents(session.getCourse().getId());

        // 获取该投票的所有投票记录（含学生和选项信息）
        List<VoteRecord> records = voteRecordRepository.findByVoteSession(session);

        // 已投票学生ID集合
        Set<Long> votedStudentIds = records.stream()
                .map(r -> r.getStudent().getId())
                .collect(Collectors.toSet());

        // 构建已投票学生列表（匿名时不显示选项文本）
        boolean isAnonymous = session.isAnonymous();
        List<VoteStudentVO.StudentInfo> votedList = records.stream()
                .map(r -> VoteStudentVO.StudentInfo.voted(
                        r.getStudent().getId(),
                        r.getStudent().getRealName(),
                        r.getStudent().getUsername(),
                        isAnonymous ? "已投票" : r.getOption().getText()))
                .collect(Collectors.toList());

        // 构建未投票学生列表
        List<VoteStudentVO.StudentInfo> notVotedList = allStudents.stream()
                .filter(s -> !votedStudentIds.contains(s.getId()))
                .map(s -> VoteStudentVO.StudentInfo.notVoted(s.getId(), s.getRealName(), s.getUsername()))
                .collect(Collectors.toList());

        VoteStudentVO vo = new VoteStudentVO();
        vo.setTotalStudents(allStudents.size());
        vo.setVotedCount(votedList.size());
        vo.setNotVotedCount(notVotedList.size());
        vo.setVotedStudents(votedList);
        vo.setNotVotedStudents(notVotedList);
        return vo;
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

    /**
     * 获取投票的学生投票明细（仅教师可查看）
     */
    public List<VoteRecordVO> getVoteRecords(Long sessionId, User teacher) {
        VoteSession session = voteSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("投票不存在"));

        if (!session.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("无权查看投票明细");
        }

        List<VoteRecord> records = voteRecordRepository.findByVoteSession(session);

        return records.stream().map(r -> {
            VoteRecordVO vo = new VoteRecordVO();
            vo.setStudentId(r.getStudent().getId());
            vo.setStudentName(r.getStudent().getRealName());
            vo.setOptionId(r.getOption().getId());
            vo.setOptionText(r.getOption().getText());
            vo.setIsCorrect(r.getOption().getIsCorrect());
            return vo;
        }).collect(Collectors.toList());
    }
}
