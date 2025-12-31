package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.batch.BatchAddRequest;
import org.can.water_law_exam_backend.dto.request.batch.BatchPageRequest;
import org.can.water_law_exam_backend.dto.request.batch.BatchUpdateRequest;
import org.can.water_law_exam_backend.dto.response.batch.BatchVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.service.ExamBatchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final ExamBatchService examBatchService;

    /**
     * 8.1.1 添加批次
     * POST /batch/add
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Long> add(@Valid @RequestBody BatchAddRequest request) {
        Long id = examBatchService.add(request);
        return Result.success("成功添加考试批次", id);
    }

    /**
     * 8.1.2 修改批次
     * POST /batch/update
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<String> update(@Valid @RequestBody BatchUpdateRequest request) {
        examBatchService.update(request);
        return Result.success("成功修改考试批次", null);
    }

    /**
     * 8.1.3 删除批次
     * POST /batch/delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody List<Long> ids) {
        int rows = examBatchService.deleteBatch(ids);
        return Result.success("成功删除" + rows + "个考试批次", null);
    }

    /**
     * 8.1.4 “分发/收回”试卷
     * POST /batch/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public Result<Boolean> distribute(@PathVariable Long id) {
        boolean distributed = examBatchService.toggleDistribute(id);
        String msg = distributed ? "成功分发试卷" : "成功收回试卷";
        return Result.success(msg, distributed);
    }

    /**
     * 8.1.5 批次列表-考试未开始
     * GET /batch/list
     */
    @GetMapping("/list")
    public Result<List<BatchVO>> list() {
        return Result.success(examBatchService.listEnabledNotStarted());
    }

    /**
     * 8.1.6 批次列表-分页（全部）
     * POST /batch/pages
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages")
    public Result<PageResult<BatchVO>> pages(@Valid @RequestBody BatchPageRequest request) {
        return Result.success(examBatchService.pages(request));
    }

    /**
     * 8.1.7 获取单个批次信息
     * GET /batch/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<BatchVO> getOne(@PathVariable Long id) {
        return Result.success(examBatchService.getOne(id));
    }

    /**
     * 8.1.8 “发布/未发布”考试
     * POST /batch/release/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/release/{id}")
    public Result<Boolean> release(@PathVariable Long id) {
        boolean released = examBatchService.toggleRelease(id);
        return Result.success("操作成功", released);
    }
}



