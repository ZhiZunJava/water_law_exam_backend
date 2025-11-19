package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.template.TemplateAddRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplatePageRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplateUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.template.TemplateVO;
import org.can.water_law_exam_backend.service.PapersTemplateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tpl")
@RequiredArgsConstructor
public class PapersTemplateController {

    private final PapersTemplateService service;

    /**
     * 7.1.1 新增组卷模板
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Integer> add(@Valid @RequestBody TemplateAddRequest request) {
        log.info("新增组卷模板: name={}", request.getTemplateName());
        int r = service.add(request);
        return Result.success("成功添加组卷模板", r);
        
    }

    /**
     * 7.1.2 修改组卷模板
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<String> update(@Valid @RequestBody TemplateUpdateRequest request) {
        log.info("修改组卷模板: id={}", request.getId());
        service.update(request);
        return Result.success("成功修改组卷模板", null);
    }

    /**
     * 7.1.3 删除组卷模板（批量）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody List<Integer> ids) {
        log.info("删除组卷模板: ids={}", ids);
        int rows = service.deleteBatch(ids);
        return Result.success("成功删除" + rows + "条数据", null);
    }

    /**
     * 7.1.4 获取单个组卷模板
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<TemplateVO> get(@PathVariable Integer id) {
        log.info("获取组卷模板: id={}", id);
        return Result.success(service.getById(id));
    }

    /**
     * 7.1.5 获取组卷模板列表（分页）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages")
    public Result<PageResult<TemplateVO>> pages(@Valid @RequestBody TemplatePageRequest request) {
        log.info("分页查询组卷模板: page={}, size={}", request.getPage(), request.getSize());
        return Result.success(service.pages(request));
    }
}
