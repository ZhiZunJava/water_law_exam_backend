package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankAddRequest;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankPageRequest;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.itembank.ItemBankVO;
import org.can.water_law_exam_backend.entity.ItemBank;
import org.can.water_law_exam_backend.service.ItemBankService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 题库管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/ib")
@RequiredArgsConstructor
public class ItemBankController {

    private final ItemBankService itemBankService;

    /**
     * 5.1 导入题库
     * 请求路径：/ib/import
     * 请求方式：POST
     *
     * @param file 上传的Excel文件
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    public Result<Void> importItemBank(@RequestParam("file") MultipartFile file) {
        log.info("导入题库：文件名={}", file.getOriginalFilename());
        int i = itemBankService.importItemBank(file);
        return Result.success("成功导入" + i + "条数据", null);
    }

    /**
     * 5.2 添加试题
     * 请求路径：/ib/add
     * 请求方式：POST
     *
     * @param request 添加请求
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Void> addItemBank(@Valid @RequestBody ItemBankAddRequest request) {
        log.info("添加试题：typeId={}, categoryId={}", request.getTypeId(), request.getCategoryId());
        itemBankService.addItemBank(request);
        return Result.success("成功添加试题", null);
    }

    /**
     * 5.3 修改试题
     * 请求路径：/ib/update
     * 请求方式：POST
     *
     * @param request 修改请求
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<Void> updateItemBank(@Valid @RequestBody ItemBankUpdateRequest request) {
        log.info("修改试题：id={}", request.getId());
        if (request.getId() == null || request.getId() <= 0) {
            return Result.error(1, "题目ID必须大于0");
        }
        itemBankService.updateItemBank(request);
        return Result.success("成功修改试题", null);
    }

    /**
     * 5.4 删除试题（批量）
     * 请求路径：/ib/delete
     * 请求方式：POST
     *
     * @param ids 题目ID数组
     * @return 操作结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<Void> deleteItemBanks(@RequestBody List<Long> ids) {
        log.info("批量删除试题：ids={}", ids);
        itemBankService.deleteItemBanks(ids);
        return Result.success("成功删除试题", null);
    }

    /**
     * 5.5 获取单条题目
     * 请求路径：/ib/{id}
     * 请求方式：GET
     *
     * @param id 题目ID
     * @return 题目信息（包含选项）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<ItemBankVO> getItemBankById(@PathVariable Long id) {
        log.info("获取单条题目：id={}", id);
        ItemBankVO itemBank = itemBankService.getItemBankById(id);
        return Result.success(itemBank);
    }

    /**
     * 5.6 获取题目列表（分页）
     * 请求路径：/ib/pages
     * 请求方式：GET
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages")
    public Result<PageResult<ItemBank>> getItemBanksByPage(@Valid @RequestBody  ItemBankPageRequest request) {
        log.info("分页查询题目列表：page={}, size={}", request.getPage(), request.getSize());
        PageResult<ItemBank> result = itemBankService.getItemBanksByPage(request);
        return Result.success(result);
    }
}

