package org.can.water_law_exam_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.entity.ItemType;
import org.can.water_law_exam_backend.service.ItemTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目类型控制器
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@RestController
@RequestMapping("/it")
@RequiredArgsConstructor
@Slf4j
public class ItemTypeController {

    private final ItemTypeService itemTypeService;

    /**
     * 获取所有题型列表
     *
     * @return 题型列表
     */
    @GetMapping("/list")
    public Result<List<ItemType>> getAllItemTypes() {
        List<ItemType> list = itemTypeService.getAllItemTypes();
        return Result.success(list);
    }

    /**
     * 根据ID获取题型信息
     *
     * @param id 题型ID
     * @return 题型信息
     */
    // @GetMapping("/{id}")
    // public Result<ItemType> getItemTypeById(@PathVariable Integer id) {
    //     ItemType itemType = itemTypeService.getItemTypeById(id);
    //     return Result.success(itemType);
    // }

    /**
     * 新增题型
     *
     * @param itemType 题型信息
     * @return 新增的题型信息
     */
    // @PostMapping
    // public Result<ItemType> addItemType(@RequestBody ItemType itemType) {
    //     ItemType result = itemTypeService.addItemType(itemType);
    //     return Result.success(result);
    // }

    /**
     * 更新题型
     *
     * @param id 题型ID
     * @param itemType 题型信息
     * @return 操作结果
     */
    // @PutMapping("/{id}")
    // public Result<Void> updateItemType(@PathVariable Integer id, @RequestBody ItemType itemType) {
    //     itemType.setId(id);
    //     itemTypeService.updateItemType(itemType);
    //     return Result.success();
    // }

    /**
     * 删除题型
     *
     * @param id 题型ID
     * @return 操作结果
     */
    // @DeleteMapping("/{id}")
    // public Result<Void> deleteItemType(@PathVariable Integer id) {
    //     itemTypeService.deleteItemType(id);
    //     return Result.success();
    // }
}

