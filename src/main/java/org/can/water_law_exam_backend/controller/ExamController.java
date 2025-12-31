package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.exam.AnswerRequest;
import org.can.water_law_exam_backend.dto.response.exam.ExamBatchUserVO;
import org.can.water_law_exam_backend.dto.response.exam.ExamPapersVO;
import org.can.water_law_exam_backend.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /**
     * 2.1 考试批次列表
     * GET /exam/batches
     */
    @GetMapping("/batches")
    public Result<List<ExamBatchUserVO>> batches() {
        return Result.success(examService.listUserBatches());
    }

    /**
     * 2.2 获取试卷内容
     * GET /exam/papers/{batchId}
     */
    @GetMapping("/papers/{batchId}")
    public Result<ExamPapersVO> papers(@PathVariable Long batchId) {
        return Result.success(examService.getPapers(batchId));
    }

    /**
     * 2.3 保存答题结果
     * POST /exam/papers
     */
    @PostMapping("/papers")
    public Result<String> saveAnswer(@Valid @RequestBody AnswerRequest request) {
        examService.saveAnswer(request);
        return Result.success(null);
    }

    /**
     * 2.4 提交试卷-交卷
     * POST /exam/submit/{bId}
     */
    @PostMapping("/submit/{bId}")
    public Result<String> submit(@PathVariable("bId") Long batchId,
                                 @Valid @RequestBody List<AnswerRequest> answers) {
        examService.submit(batchId, answers);
        return Result.success(null);
    }

    /**
     * 2.5 获取可以报名的考试批次列表
     * GET /exam/ebs
     */
    @GetMapping("/ebs")
    public Result<List<ExamBatchUserVO>> joinableBatches() {
        return Result.success(examService.listJoinableBatches());
    }

    /**
     * 2.6 考试报名
     * POST /exam/join/{batchId}
     */
    @PostMapping("/join/{batchId}")
    public Result<String> join(@PathVariable Long batchId) {
        examService.join(batchId);
        return Result.success("考试报名成功", null);
    }

    /**
     * 2.7 开始考试
     * POST /exam/start/{batchId}
     */
    @PostMapping("/start/{batchId}")
    public Result<String> start(@PathVariable Long batchId) {
        examService.start(batchId);
        return Result.success("请开始答题", null);
    }
}



