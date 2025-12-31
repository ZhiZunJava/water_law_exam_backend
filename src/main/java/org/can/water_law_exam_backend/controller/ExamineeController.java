package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineeOptionalPageRequest;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineePageRequest;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineeReviewRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.examinee.ExamineePageVO;
import org.can.water_law_exam_backend.service.ExamineeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emn")
@RequiredArgsConstructor
public class ExamineeController {

    private final ExamineeService examineeService;

    /**
     * 8.2.1 导入考生
     * POST /emn/import
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public Result<String> importExaminee(@RequestParam("file") MultipartFile file,
                                         @RequestParam("id") Long batchId) {
        int cnt = examineeService.importExaminee(file, batchId);
        return Result.success("成功导入" + cnt + "条考生数据", null);
    }

    /**
     * 8.2.2 添加考生
     * POST /emn/bind/{batchId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bind/{batchId}")
    public Result<String> bind(@PathVariable Long batchId,
                               @RequestBody List<Long> userIds) {
        int cnt = examineeService.bindExaminees(batchId, userIds);
        return Result.success("成功添加" + cnt + "个考生到考试中", null);
    }

    /**
     * 8.2.3 考生检索（分页）
     * POST /emn/pages
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages")
    public Result<PageResult<ExamineePageVO>> pages(@Valid @RequestBody ExamineePageRequest request) {
        return Result.success(examineeService.pages(request));
    }

    /**
     * 8.2.4 移除考生
     * POST /emn/rf/{batchId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rf/{batchId}")
    public Result<String> remove(@PathVariable Long batchId,
                                 @RequestBody List<Long> userIds) {
        int cnt = examineeService.removeExaminees(batchId, userIds);
        return Result.success("成功移除" + cnt + "位考生", null);
    }

    /**
     * 8.2.5 可添加考生列表（分页）
     * POST /emn/optUsers
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/optUsers")
    public Result<PageResult<ExamineePageVO>> optionalUsers(@Valid @RequestBody ExamineeOptionalPageRequest request) {
        return Result.success(examineeService.optionalPages(request));
    }

    /**
     * 8.2.6 考试报名审核
     * POST /emn/review
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/review")
    public Result<String> review(@Valid @RequestBody ExamineeReviewRequest request) {
        examineeService.review(request);
        return Result.success("审核完成", null);
    }
}



