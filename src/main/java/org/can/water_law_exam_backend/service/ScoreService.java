package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.excel.ScoreExportRow;
import org.can.water_law_exam_backend.dto.request.score.ScorePageRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.papers.PapersStructVO;
import org.can.water_law_exam_backend.dto.response.score.ScoreAnswerItemVO;
import org.can.water_law_exam_backend.dto.response.score.ScoreAnswerOptionVO;
import org.can.water_law_exam_backend.dto.response.score.ScoreDetailVO;
import org.can.water_law_exam_backend.dto.response.score.ScorePageVO;
import org.can.water_law_exam_backend.entity.*;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ExamScoreMapper examScoreMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final ExamBatchMapper examBatchMapper;
    private final AccountUserMapper accountUserMapper;
    private final PapersMapper papersMapper;
    private final PapersStructMapper papersStructMapper;
    private final PapersContentMapper papersContentMapper;
    private final ItemOptionMapper itemOptionMapper;
    private final ExamineeMapper examineeMapper;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ItemBankMapper itemBankMapper;

    /**
     * 8.3.1 成绩检索
     */
    public PageResult<ScorePageVO> pages(Long batchId, ScorePageRequest request) {
        if (batchId == null || batchId <= 0) {
            throw new BusinessException(1, "考试批次ID不正确");
        }
        String key = request.getParam() != null ? request.getParam().getKey() : null;
        Integer c = request.getParam() != null ? request.getParam().getC() : null;
        PageHelper.startPage(request.getPage(), request.getSize());
        List<ExamScore> list = examScoreMapper.selectByBatchAndFilter(batchId, key, c);
        PageInfo<ExamScore> pi = new PageInfo<>(list);

        List<ScorePageVO> vos = new ArrayList<>();
        for (ExamScore s : list) {
            AccountUser u = accountUserMapper.selectById(s.getUserId());
            if (u == null) continue;
            ScorePageVO vo = new ScorePageVO();
            vo.setUserId(u.getId());
            vo.setUserName(u.getName());
            vo.setOrg(accountUserMapper.selectOrgNameById(u.getOrgId()));
            vo.setIdNo(u.getIdNo());
            vo.setPhone(u.getPhone());
            vo.setScore(s.getTotalScore());
            vo.setSubmitted(s.getSubmitTime() != null);
            vos.add(vo);
        }
        PageInfo<ScorePageVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    /**
     * 8.3.2 成绩导出（仅导出合格学员）
     * 生成 Excel（.xlsx）
     */
    @Transactional(readOnly = true)
    public void exportPass(Long batchId, HttpServletResponse response) {
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        List<ExamScore> scores = examScoreMapper.selectPassByBatch(batchId);
        String fileName = "score_batch_" + batchId + ".xlsx";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            // 构造导出数据
            List<ScoreExportRow> rows = new ArrayList<>();
            for (ExamScore s : scores) {
                AccountUser u = accountUserMapper.selectById(s.getUserId());
                if (u == null) continue;
                ScoreExportRow row = new ScoreExportRow();
                row.setName(safe(u.getName()));
                row.setOrg(safe(accountUserMapper.selectOrgNameById(u.getOrgId())));
                row.setIdNo(safe(u.getIdNo()));
                row.setPhone(safe(u.getPhone()));
                row.setTotalScore(s.getTotalScore());
                row.setPass(Boolean.TRUE.equals(s.getIsPass()) ? "是" : "否");
                row.setSubmitTime(s.getSubmitTime() == null ? "" : s.getSubmitTime().format(DATETIME_FMT));
                rows.add(row);
            }

            // 使用 FastExcel 写出
            cn.idev.excel.FastExcel.write(response.getOutputStream(), ScoreExportRow.class)
                    .sheet("成绩")
                    .doWrite(rows);
        } catch (Exception e) {
            log.error("导出成绩失败", e);
            throw new BusinessException(1, "导出成绩失败：" + e.getMessage());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * 8.3.3 获取考生试卷答题明细
     */
    public ScoreDetailVO detail(Long batchId, Long userId) {
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        // 根据考生在该批次下的分配序号，找到他对应的具体试卷
        Examinee ex = examineeMapper.selectByUserAndBatch(userId, batchId).stream().findFirst()
                .orElseThrow(() -> new BusinessException(1, "考生未报名该考试"));
        ExamScore examScore = examScoreMapper.selectByBatchAndUser(batchId, userId);
        if (examScore == null || examScore.getSubmitTime() == null) {
            throw new BusinessException(1, "请完成交卷后再查看试卷明细");
        }
        Long groupId = batch.getPapersId();
        List<Papers> candidate = papersMapper.selectByGroupId(groupId);
        if (candidate == null || candidate.isEmpty()) {
            throw new BusinessException(1, "试卷组内没有可用试卷");
        }
        Integer papersNo = ex.getPapersNo() != null ? ex.getPapersNo() : candidate.get(0).getPapersNo();
        Papers papers = papersMapper.selectByGroupAndNo(groupId, papersNo);
        if (papers == null) {
            throw new BusinessException(1, "对应考生的试卷不存在");
        }
        ScoreDetailVO vo = new ScoreDetailVO();
        vo.setBatchId(batchId);
        vo.setTitle(papers.getPapersTitle());
        vo.setTotalScore(papers.getTotalScore());
        vo.setStartTime(batch.getStartTime() == null ? null : batch.getStartTime().format(DATETIME_FMT));
        vo.setEndTime(batch.getEndTime() == null ? null : batch.getEndTime().format(DATETIME_FMT));
        vo.setPrepareMinutes(batch.getPrepareMinutes());

        List<PapersStruct> structs = papersStructMapper.selectByPapersId(papers.getId());
        List<PapersStructVO> structVOs = new ArrayList<>();
        for (PapersStruct s : structs) {
            PapersStructVO sv = new PapersStructVO();
            sv.setPapersId(s.getPapersId());
            sv.setTypeName(s.getTypeName());
            sv.setTypeRemarks(s.getTypeRemarks());
            sv.setScore(s.getScore());
            structVOs.add(sv);
        }
        vo.setStructs(structVOs);

        // 组装答题明细（按题型分组）
        List<ExamAnswer> answers = examAnswerMapper.selectByBatchAndUser(batchId, userId);
        Map<Long, ExamAnswer> answerMap = new HashMap<>();
        for (ExamAnswer a : answers) {
            answerMap.put(a.getItemId(), a);
        }
        List<PapersContent> pcs = papersContentMapper.selectByPapersIdAndNo(papers.getId(), papers.getPapersNo());
        Map<String, List<ScoreAnswerItemVO>> content = new LinkedHashMap<>();
        for (PapersContent pc : pcs) {
            ItemBank ibFull = itemBankMapper.selectById(pc.getItemId());
            if (ibFull == null) continue;
            List<ItemOption> opts = itemOptionMapper.selectByItemId(pc.getItemId());
            List<Integer> correct = new ArrayList<>();
            List<ScoreAnswerOptionVO> optVOs = new ArrayList<>();
            for (ItemOption op : opts) {
                if (Boolean.TRUE.equals(op.getIsCorrect())) {
                    correct.add(op.getOptionNo());
                }
            }
            ExamAnswer ans = answerMap.get(pc.getItemId());
            List<Integer> chosen = parseChosen(ans);
            for (ItemOption op : opts) {
                ScoreAnswerOptionVO ov = new ScoreAnswerOptionVO();
                ov.setNo(op.getOptionNo());
                ov.setTitle(op.getOptionTitle());
                ov.setCorrect(Boolean.TRUE.equals(op.getIsCorrect()));
                ov.setChosen(chosen.contains(op.getOptionNo()));
                optVOs.add(ov);
            }
            ScoreAnswerItemVO itemVO = new ScoreAnswerItemVO();
            itemVO.setId(pc.getItemId());
            itemVO.setScore(pc.getScore());
            itemVO.setTypeName(ibFull.getTypeName());
            itemVO.setContent(ibFull.getContent());
            itemVO.setOptions(optVOs);
            itemVO.setUserAnswer(chosen.isEmpty() ? null : chosen);
            itemVO.setCorrectAnswer(correct.isEmpty() ? null : correct);
            itemVO.setIsCorrect(!correct.isEmpty() && new HashSet<>(correct).equals(new HashSet<>(chosen)));

            String typeName = ibFull.getTypeName();
            content.computeIfAbsent(typeName, k -> new ArrayList<>()).add(itemVO);
        }
        vo.setContent(content);

        return vo;
    }

    /**
     * 解析考生答题内容，转换为选项编号集合
     * 支持格式：
     * 1. 数组字符串："[1,3,5]"（多选题/单选题）
     * 2. 单个数字字符串："2"（单选题）
     * 3. 判断题特殊格式："1"（正确）/"0"（错误），会映射为选项1/2
     * 4. null/空字符串：返回空集合（未作答）
     *
     * @param ans 考生答题记录（可能为null）
     * @return 选项编号集合（空集合表示未作答）
     */
    private List<Integer> parseChosen(ExamAnswer ans) {
        List<Integer> chosen = new ArrayList<>();
        if (ans == null || ans.getAnswerContent() == null) {
            return chosen;
        }

        String rawAnswer = ans.getAnswerContent().trim();
        if (rawAnswer.isEmpty()) {
            return chosen;
        }

        // 处理数组格式（如 "[1,3]"）
        if (rawAnswer.startsWith("[") && rawAnswer.endsWith("]")) {
            String content = rawAnswer.substring(1, rawAnswer.length() - 1).trim();
            if (!content.isEmpty()) {
                String[] optionStrs = content.split(",");
                for (String str : optionStrs) {
                    parseOptionNumber(str.trim(), chosen);
                }
            }
        } else {
            // 处理单个选项格式（如 "2"、"1"、"0"）
            parseOptionNumber(rawAnswer, chosen);
        }

        // 去重并保持顺序（防止重复选择同一选项）
        return new ArrayList<>(new LinkedHashSet<>(chosen));
    }

    /**
     * 解析单个选项字符串为数字，处理判断题特殊映射
     * 判断题规则：前端传 1=正确（对应选项1），0=错误（对应选项2）
     *
     * @param str    选项字符串（如 "1"、"0"、"3"）
     * @param chosen 存储结果的集合
     */
    private void parseOptionNumber(String str, List<Integer> chosen) {
        if (str.isEmpty()) {
            return;
        }

        try {
            int optionNo = Integer.parseInt(str);
            // 判断题特殊映射（0→2）
            if (optionNo == 0) {
                chosen.add(2);
            } else if (optionNo > 0) { // 只允许正整数选项
                chosen.add(optionNo);
            }
        } catch (NumberFormatException e) {
            log.warn("无效的选项编号格式：{}", str);
            // 忽略无效格式，不添加到结果中
        }
    }

    /**
     * 计算并保存某个考生在某批次的成绩
     *
     * @param batchId    批次ID
     * @param userId     学员ID
     * @param submitTime 交卷时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void evaluateAndSaveScore(Long batchId, Long userId, java.time.LocalDateTime submitTime) {
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        // 当前按考生在试卷组中的分配序号进行评分
        Examinee ex = examineeMapper.selectByUserAndBatch(userId, batchId).stream().findFirst()
                .orElseThrow(() -> new BusinessException(1, "考生未报名该考试"));
        Long groupId = batch.getPapersId();
        List<Papers> candidate = papersMapper.selectByGroupId(groupId);
        if (candidate == null || candidate.isEmpty()) {
            throw new BusinessException(1, "试卷组内没有可用试卷");
        }
        Integer papersNo = ex.getPapersNo() != null ? ex.getPapersNo() : candidate.get(0).getPapersNo();
        Papers papers = papersMapper.selectByGroupAndNo(groupId, papersNo);
        if (papers == null) {
            throw new BusinessException(1, "对应考生的试卷不存在");
        }
        // 获取试卷内容（所有题目）
        List<PapersContent> contents = papersContentMapper.selectByPapersIdAndNo(papers.getId(), papersNo);
        if (contents.isEmpty()) {
            throw new BusinessException(1, "试卷未配置试题");
        }
        // 获取该考生所有答题记录
        List<ExamAnswer> answers = examAnswerMapper.selectByBatchAndUser(batchId, userId);
        Map<Long, ExamAnswer> answerMap = new HashMap<>();
        for (ExamAnswer a : answers) {
            answerMap.put(a.getItemId(), a);
        }

        double totalScore = 0.0;

        for (PapersContent pc : contents) {
            Long itemId = pc.getItemId();
            ExamAnswer ans = answerMap.get(itemId);
            if (ans == null || ans.getAnswerContent() == null) {
                continue; // 未作答，得分0
            }
            // 解析答案内容，格式形如 "[1, 4]"
            Set<Integer> chosen = new HashSet<>();
            String raw = ans.getAnswerContent().replace("[", "").replace("]", "").trim();
            if (!raw.isEmpty()) {
                String[] parts = raw.split(",");
                for (String p : parts) {
                    try {
                        String t = p.trim();
                        if (!t.isEmpty()) {
                            chosen.add(Integer.parseInt(t));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (chosen.isEmpty()) continue;

            // 判断题特殊处理：前端约定 1=正确，0=错误
            if (pc.getTypeId() != null && pc.getTypeId() == 3) {
                Set<Integer> mapped = new HashSet<>();
                for (Integer c : chosen) {
                    if (c == null) continue;
                    if (c == 1) {
                        mapped.add(1); // 选“正确”
                    } else if (c == 0) {
                        mapped.add(2); // 映射到选项2（错误）
                    } else {
                        // 兜底：如果前端直接传1/2，也能正常识别
                        mapped.add(c);
                    }
                }
                chosen = mapped;
            }

            // 获取正确选项集合
            List<ItemOption> opts = itemOptionMapper.selectByItemId(itemId);
            Set<Integer> correct = new HashSet<>();
            for (ItemOption o : opts) {
                if (Boolean.TRUE.equals(o.getIsCorrect())) {
                    correct.add(o.getOptionNo());
                }
            }
            boolean isCorrect = !correct.isEmpty() && correct.equals(chosen);
            if (isCorrect) {
                totalScore += pc.getScore().doubleValue();
            }
            ExamAnswer answersRecode = examAnswerMapper.selectById(ans.getId());
            if (answersRecode != null) {
                answersRecode.setIsCorrect(isCorrect);
                answersRecode.setScore(isCorrect ? pc.getScore().doubleValue() : 0);
                examAnswerMapper.update(answersRecode);
            }
        }

        // 及格线，暂时固定为60（也可以从tb_sys_config读取）
        double passScore = 60.0;
        boolean isPass = totalScore + 1e-6 >= passScore;

        // 计算考试时长（分钟）
        Integer duration = null;
        if (ex.getExamStartTime() != null && submitTime != null) {
            duration = (int) Duration.between(ex.getExamStartTime(), submitTime).toMinutes();
            if (duration < 0) duration = 0;
        }

        ExamScore existing = examScoreMapper.selectByBatchAndUser(batchId, userId);
        if (existing == null) {
            ExamScore es = new ExamScore();
            es.setBatchId(batchId);
            es.setUserId(userId);
            es.setTotalScore(totalScore);
            es.setPassScore(passScore);
            es.setIsPass(isPass);
            es.setExamDuration(duration);
            es.setSubmitTime(submitTime);
            examScoreMapper.insert(es);
        } else {
            existing.setTotalScore(totalScore);
            existing.setPassScore(passScore);
            existing.setIsPass(isPass);
            existing.setExamDuration(duration);
            existing.setSubmitTime(submitTime);
            examScoreMapper.update(existing);
        }
    }
}