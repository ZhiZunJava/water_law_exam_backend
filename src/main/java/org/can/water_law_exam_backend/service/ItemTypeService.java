package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.entity.ItemType;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.ItemTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题型服务类
 *
 * @author can
 * @date 2025/10/28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemTypeService {

    private final ItemTypeMapper itemTypeMapper;

    /**
     * 获取所有题型列表
     *
     * @return 题型列表
     */
    public List<ItemType> getAllItemTypes() {
        log.info("获取所有题型列表");
        List<ItemType> list = itemTypeMapper.selectAll();
        log.info("获取题型列表成功，共{}条记录", list.size());
        return list;
    }

    /**
     * 根据ID获取题型信息
     *
     * @param id 题型ID
     * @return 题型信息
     */
    // public ItemType getItemTypeById(Integer id) {
    //     log.info("获取题型信息：id={}", id);
    //     ItemType itemType = itemTypeMapper.selectById(id);
    //     if (itemType == null) {
    //         throw new BusinessException(404, "题型不存在");
    //     }
    //     return itemType;
    // }

    /**
     * 新增题型
     *
     * @param itemType 题型信息
     * @return 新增的题型信息
     */
    // @Transactional(rollbackFor = Exception.class)
    // public ItemType addItemType(ItemType itemType) {
    //     log.info("新增题型：typeName={}", itemType.getTypeName());
        
    //     // 参数校验
    //     if (itemType.getTypeName() == null || itemType.getTypeName().trim().isEmpty()) {
    //         throw new BusinessException(1, "题型名称不能为空");
    //     }

    //     int rows = itemTypeMapper.insert(itemType);
    //     if (rows == 0) {
    //         throw new BusinessException(1, "新增题型失败");
    //     }

    //     log.info("新增题型成功：id={}, typeName={}", itemType.getId(), itemType.getTypeName());
    //     return itemType;
    // }

    /**
     * 更新题型
     *
     * @param itemType 题型信息
     */
    // @Transactional(rollbackFor = Exception.class)
    // public void updateItemType(ItemType itemType) {
    //     log.info("更新题型：id={}", itemType.getId());

    //     // 检查题型是否存在
    //     ItemType existingItemType = itemTypeMapper.selectById(itemType.getId());
    //     if (existingItemType == null) {
    //         throw new BusinessException(404, "题型不存在");
    //     }

    //     // 参数校验
    //     if (itemType.getTypeName() == null || itemType.getTypeName().trim().isEmpty()) {
    //         throw new BusinessException(1, "题型名称不能为空");
    //     }

    //     int rows = itemTypeMapper.update(itemType);
    //     if (rows == 0) {
    //         throw new BusinessException(1, "更新题型失败");
    //     }

    //     log.info("更新题型成功：id={}, typeName={}", itemType.getId(), itemType.getTypeName());
    // }

    /**
     * 删除题型
     *
     * @param id 题型ID
     */
    // @Transactional(rollbackFor = Exception.class)
    // public void deleteItemType(Integer id) {
    //     log.info("删除题型：id={}", id);

    //     // 检查题型是否存在
    //     ItemType itemType = itemTypeMapper.selectById(id);
    //     if (itemType == null) {
    //         throw new BusinessException(404, "题型不存在");
    //     }

    //     // TODO: 检查是否有题目使用了该题型，如果有则不允许删除

    //     int rows = itemTypeMapper.deleteById(id);
    //     if (rows == 0) {
    //         throw new BusinessException(1, "删除题型失败");
    //     }

    //     log.info("删除题型成功：id={}, typeName={}", id, itemType.getTypeName());
    // }
}

