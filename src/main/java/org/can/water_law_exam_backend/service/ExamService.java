package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.exam.AnswerRequest;
import org.can.water_law_exam_backend.dto.response.exam.ExamBatchUserVO;
import org.can.water_law_exam_backend.dto.response.exam.ExamPapersVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentVO;
import org.can.water_law_exam_backend.entity.*;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.*;
import org.can.water_law_exam_backend.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamineeMapper examineeMapper;
    private final ExamBatchMapper examBatchMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final AccountUserMapper accountUserMapper;
    private final PapersMapper papersMapper;
    private final PapersService papersService;
    private final ScoreService scoreService;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException(1, "未登录或登录状态已失效");
        }
        return ((LoginUser) auth.getPrincipal()).getUserId();
    }

    /**
     * 2.1 考试批次列表（考生端）
     */
    public List<ExamBatchUserVO> listUserBatches() {
        Long userId = currentUserId();
        LocalDateTime now = LocalDateTime.now();
        // 查询当前用户的所有报名记录
        List<Examinee> examinees = examineeMapper.selectByUserId(userId);
        List<ExamBatchUserVO> result = new ArrayList<>();
        for (Examinee e : examinees) {
            ExamBatch b = examBatchMapper.selectById(e.getBatchId());
            if (b == null) continue;
            // 条件：审核通过、已发布且试卷已分发、在考试截止时间窗口内（这里简单使用 endTime 作为截止）
            if (!Integer.valueOf(1).equals(e.getReviewStatus())) continue;
            if (!Boolean.TRUE.equals(b.getReleased()) || !Boolean.TRUE.equals(b.getPapersDistributed())) continue;
            if (b.getEndTime() != null && now.isAfter(b.getEndTime())) continue;

            ExamBatchUserVO vo = new ExamBatchUserVO();
            vo.setId(b.getId());
            vo.setBatchName(b.getBatchName());
            vo.setStartTime(b.getStartTime() == null ? null : b.getStartTime().format(DATETIME_FMT));
            vo.setEndTime(b.getEndTime() == null ? null : b.getEndTime().format(DATETIME_FMT));
            vo.setLateMinutes(b.getLateMinutes());
            vo.setAdvanceMinutes(b.getAdvanceMinutes());
            vo.setJoined(true);
            vo.setStarted(Boolean.TRUE.equals(e.getExamStarted()));
            vo.setSubmitted(Boolean.TRUE.equals(e.getSubmitted()));
            result.add(vo);
        }
        return result;
    }

    /**
     * 2.2 获取试卷内容（考生端）
     */
    public ExamPapersVO getPapers(Long batchId) {
        Long userId = currentUserId();
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        // 校验报名及审核状态
        List<Examinee> list = examineeMapper.selectByBatch(batchId, null, null);
        Examinee ex = list.stream().filter(e -> e.getUserId().equals(userId)).findFirst()
                .orElseThrow(() -> new BusinessException(1, "未报名该考试"));
        if (!Integer.valueOf(1).equals(ex.getReviewStatus())) {
            throw new BusinessException(1, "报名尚未审核通过");
        }
        LocalDateTime now = LocalDateTime.now();
        // 简化时间窗口：在开始前 prepareMinutes 内或考试时间内
        LocalDateTime preStart = batch.getStartTime().minusMinutes(batch.getPrepareMinutes() == null ? 0 : batch.getPrepareMinutes());
        if (now.isBefore(preStart) || now.isAfter(batch.getEndTime())) {
            throw new BusinessException(1, "不在可查看试卷时间范围内");
        }

        // 确保该考生在本批次下已分配试卷序号（papersNo），用于在试卷组内随机分配
        assignPapersNoIfNeeded(batch, ex);
        Long groupId = batch.getPapersId();
        Integer papersNo = ex.getPapersNo();
        PapersContentVO pc = papersService.content(groupId, papersNo, false);

        ExamPapersVO vo = new ExamPapersVO();
        vo.setBatchId(batchId);
        vo.setTitle(pc.getTitle());
        vo.setTotalScore(pc.getTotalScore());
        vo.setStartTime(batch.getStartTime() == null ? null : batch.getStartTime().format(DATETIME_FMT));
        vo.setEndTime(batch.getEndTime() == null ? null : batch.getEndTime().format(DATETIME_FMT));
        vo.setPrepareMinutes(batch.getPrepareMinutes());
        vo.setStructs(pc.getStructs());
        vo.setContent(pc.getContent());
        return vo;
    }

    /**
     * 2.3 保存答题结果（实时保存）
     * 这里只做答案持久化，不在此处计算得分。
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAnswer(AnswerRequest req) {
        Long userId = currentUserId();
        // 查找当前用户所有未提交的考试，取最近一条
        List<Examinee> list = examineeMapper.selectUnsubmittedByUser(userId);
        if (list.isEmpty()) {
            throw new BusinessException(1, "当前没有正在进行的考试");
        }
        Examinee ex = list.get(0);
        Boolean isExamStarted = ex.getExamStarted();
        if (isExamStarted == null || !isExamStarted) {
            throw new BusinessException(1, "没有开始考试，禁止答题！");
        }
        Long batchId = ex.getBatchId();

        // 考试已结束则不允许再答题
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null || batch.getEndTime() == null) {
            throw new BusinessException(1, "考试批次信息不完整");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(batch.getEndTime())) {
            throw new BusinessException(1, "考试已结束，不能继续答题");
        }

        ExamAnswer ans = new ExamAnswer();
        ans.setBatchId(batchId);
        ans.setUserId(userId);
        ans.setItemId(req.getId());
        // 直接使用List.toString() 作为JSON数组字符串（如 [1, 4]）
        ans.setAnswerContent(req.getAns().toString());
        ans.setUpdateTime(LocalDateTime.now());

        // 删除旧记录再插入，保证每题一条记录
        examAnswerMapper.deleteByBatchUserAndItem(batchId, userId, req.getId());
        examAnswerMapper.insert(ans);
    }

    /**
     * 2.4 提交试卷-交卷
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long batchId, List<AnswerRequest> answers) {
        Long userId = currentUserId();
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = batch.getEndTime();
        if (end == null) {
            throw new BusinessException(1, "考试结束时间未配置");
        }
        // 结束后30分钟内允许提交
        int SUBMIT_DELAY_MINUTES = 30;
        LocalDateTime latestSubmit = end.plusMinutes(SUBMIT_DELAY_MINUTES);
        if (now.isAfter(latestSubmit)) {
            throw new BusinessException(1, "已超过允许提交时间，无法交卷");
        }

        List<Examinee> list = examineeMapper.selectByUserAndBatch(userId, batchId);
        Examinee ex = list.stream().findFirst()
                .orElseThrow(() -> new BusinessException(1, "未报名该考试"));

        Boolean isExamStarted = ex.getExamStarted();
        if (isExamStarted == null || !isExamStarted) {
            throw new BusinessException(1, "没有开始考试，禁止答题！");
        }
        if (Boolean.TRUE.equals(ex.getSubmitted())) {
            throw new BusinessException(1, "请勿重复提交试卷");
        }

        // 考试尚未结束：以本次携带答案为主，缺失题目使用服务器已保存结果
        if (!now.isAfter(end)) {
            if (answers != null) {
                for (AnswerRequest req : answers) {
                    saveAnswer(req);
                }
            }
        }
        // 考试已结束但在允许提交窗口内：忽略本次携带答案，仅使用服务器过程记录
        // 这里不调用 saveAnswer

        ex.setSubmitted(true);
        ex.setSubmitTime(now);
        ex.setExamStarted(true);
        if (ex.getExamStartTime() == null) {
            ex.setExamStartTime(now);
        }
        List<Examinee> one = new ArrayList<>();
        one.add(ex);
        examineeMapper.insertBatch(one);

        // 计算并保存成绩（按照该考生在试卷组中的分配序号评分）
        scoreService.evaluateAndSaveScore(batchId, userId, now);
    }

    /**
     * 2.5 获取可以报名的考试批次列表
     */
    public List<ExamBatchUserVO> listJoinableBatches() {
        Long userId = currentUserId();
        LocalDateTime now = LocalDateTime.now();
        // 已发布、未分发试卷、考试未开始
        List<ExamBatch> batches = examBatchMapper.selectJoinable(now);
        List<ExamBatchUserVO> result = new ArrayList<>();
        for (ExamBatch b : batches) {
            ExamBatchUserVO vo = new ExamBatchUserVO();
            vo.setId(b.getId());
            vo.setBatchName(b.getBatchName());
            vo.setStartTime(b.getStartTime() == null ? null : b.getStartTime().format(DATETIME_FMT));
            vo.setEndTime(b.getEndTime() == null ? null : b.getEndTime().format(DATETIME_FMT));
            vo.setLateMinutes(b.getLateMinutes());
            vo.setAdvanceMinutes(b.getAdvanceMinutes());
            // joined：检查是否已报名
            List<Examinee> exs = examineeMapper.selectByBatch(b.getId(), null, null)
                    .stream().filter(e -> e.getUserId().equals(userId)).collect(Collectors.toList());
            vo.setJoined(!exs.isEmpty());
            result.add(vo);
        }
        return result;
    }

    /**
     * 2.6 考试报名
     */
    @Transactional(rollbackFor = Exception.class)
    public void join(Long batchId) {
        Long userId = currentUserId();
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (batch.getStartTime() != null && !now.isBefore(batch.getStartTime())) {
            throw new BusinessException(1, "考试已开始或已结束，无法报名");
        }
        // 检查是否已报名
        List<Examinee> exs = examineeMapper.selectByUserAndBatch(userId, batchId);
        if (!exs.isEmpty()) {
            throw new BusinessException(1, "已报名该考试");
        }
        Examinee ex = new Examinee();
        ex.setBatchId(batchId);
        ex.setUserId(userId);
        ex.setReviewStatus(Boolean.TRUE.equals(batch.getReviewRequired()) ? 0 : 1);
        // 随机分配试卷序号
        assignPapersNoIfNeeded(batch, ex);
        List<Examinee> list = new ArrayList<>();
        list.add(ex);
        examineeMapper.insertBatch(list);
    }

    /**
     * 2.7 开始考试
     */
    @Transactional(rollbackFor = Exception.class)
    public void start(Long batchId) {
        Long userId = currentUserId();
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime preStart = batch.getStartTime().minusMinutes(batch.getPrepareMinutes() == null ? 0 : batch.getPrepareMinutes());
        if (now.isBefore(preStart) || now.isAfter(batch.getEndTime())) {
            throw new BusinessException(1, "当前不在允许开始考试的时间范围内");
        }
        List<Examinee> list = examineeMapper.selectByUserAndBatch(userId, batchId);
        if (list.isEmpty()) {
            throw new BusinessException(1, "未报名该考试");
        }
        Examinee ex = list.get(0);
        ex.setExamStarted(true);
        if (ex.getExamStartTime() == null) {
            ex.setExamStartTime(now);
        }
        // 如果还未分配试卷序号，在这里补一次（极端情况下有用）
        assignPapersNoIfNeeded(batch, ex);
        List<Examinee> one = new ArrayList<>();
        one.add(ex);
        examineeMapper.insertBatch(one);
    }

    /**
     * 为考生在指定批次下分配试卷序号（papersNo）
     * 规则：在该批次绑定的试卷组内随机选择一套卷子，将其 papers_no 记录到 tb_examinee.papers_no
     */
    private void assignPapersNoIfNeeded(ExamBatch batch, Examinee ex) {
        if (ex.getPapersNo() != null) {
            return;
        }
        Long groupId = batch.getPapersId();
        List<Papers> papersList = papersMapper.selectByGroupId(groupId);
        if (papersList == null || papersList.isEmpty()) {
            throw new BusinessException(1, "试卷组内没有可用试卷");
        }
        int idx = ThreadLocalRandom.current().nextInt(papersList.size());
        Integer papersNo = papersList.get(idx).getPapersNo();
        ex.setPapersNo(papersNo);
    }
}


