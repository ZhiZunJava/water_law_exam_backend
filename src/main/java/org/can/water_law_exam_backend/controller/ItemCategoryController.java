package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.itemcategory.ItemCategoryAddRequest;
import org.can.water_law_exam_backend.dto.request.itemcategory.ItemCategoryUpdateRequest;
import org.can.water_law_exam_backend.dto.response.itemcategory.ItemCategoryVO;
import org.can.water_law_exam_backend.service.ItemCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ic")
@RequiredArgsConstructor
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    /**
     * 获取题目分类树
     */
    @GetMapping("/tree")
    public Result<List<ItemCategoryVO>> getTree() {
        List<ItemCategoryVO> tree = itemCategoryService.getCategoryTree();
        return Result.success(tree);
    }

    /**
     * 获取单个题目分类
     */
    @GetMapping("/{id}")
    public Result<ItemCategoryVO> getById(@PathVariable Integer id) {
        ItemCategoryVO vo = itemCategoryService.getById(id);
        return Result.success(vo);
    }

    /**
     * 修改题目分类
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody ItemCategoryUpdateRequest request) {
        itemCategoryService.update(request);
        return Result.success("成功修改分类信息", null);
    }

    /**
     * 添加题目分类
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody ItemCategoryAddRequest request) {
        itemCategoryService.add(request);
        return Result.success("成功添加分类信息", null);
    }

    /**
     * 批量删除题目分类（忽略无法删除的）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        int success = itemCategoryService.deleteBatch(ids);
        return Result.success("成功删除" + success + "条数据", null);
    }

    /**
     * 删除单个题目分类（忽略无法删除的）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteOne(@PathVariable Integer id) {
        boolean ok = itemCategoryService.deleteOne(id);
        String msg = ok ? "成功删除数据" : "忽略未删除数据";
        return Result.success(msg, null);
    }
}


