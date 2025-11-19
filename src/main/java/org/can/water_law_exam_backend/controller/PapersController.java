package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.papers.PapersCreateRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplatePageRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.papers.PapersAbstractVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersListVO;
import org.can.water_law_exam_backend.service.PapersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pps")
@RequiredArgsConstructor
public class PapersController {

    private final PapersService service;

    /**
     * 7.2.1 组卷
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public Result<String> create(@Valid @RequestBody PapersCreateRequest request) {
        int cnt = service.create(request);
        return Result.success("成功生成" + cnt + "份试卷", null);
    }

    /**
     * 7.2.2 获取试卷列表（分页）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages")
    public Result<PageResult<PapersListVO>> pages(@Valid @RequestBody TemplatePageRequest request) {
        return Result.success(service.pages(request));
    }

    /**
     * 7.2.3 删除试卷
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody List<Long> ids) {
        int rows = service.deleteBatch(ids);
        return Result.success("成功删除" + rows + "条数据", null);
    }

    /**
     * 7.2.4 获取试卷摘要信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/abstract/{id}")
    public Result<PapersAbstractVO> abstractInfo(@PathVariable Long id) {
        return Result.success(service.abstractInfo(id));
    }

    /**
     * 7.2.5 获取试卷试题内容
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ctt/{id}/{no}")
    public Result<PapersContentVO> content(@PathVariable Long id, @PathVariable Integer no) {
        return Result.success(service.content(id, no));
    }
}
