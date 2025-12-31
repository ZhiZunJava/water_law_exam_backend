package org.can.water_law_exam_backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.score.ScorePageRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.score.ScoreDetailVO;
import org.can.water_law_exam_backend.dto.response.score.ScorePageVO;
import org.can.water_law_exam_backend.service.ScoreService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 8.3.1 成绩检索
     * POST /score/pages/{batchId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages/{batchId}")
    public Result<PageResult<ScorePageVO>> pages(@PathVariable Long batchId,
                                                 @Valid @RequestBody ScorePageRequest request) {
        return Result.success(scoreService.pages(batchId, request));
    }

    /**
     * 8.3.2 成绩导出
     * POST /score/export/{batchId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/export/{batchId}")
    public void export(@PathVariable Long batchId, HttpServletResponse response) {
        scoreService.exportPass(batchId, response);
    }

    /**
     * 8.3.3 获取考生试卷答题明细
     * GET /score/{batchId}/{userId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{batchId}/{userId}")
    public Result<ScoreDetailVO> detail(@PathVariable Long batchId,
                                        @PathVariable Long userId) {
        return Result.success(scoreService.detail(batchId, userId));
    }
}



