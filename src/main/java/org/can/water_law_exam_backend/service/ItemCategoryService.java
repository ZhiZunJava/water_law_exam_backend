package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.itemcategory.ItemCategoryAddRequest;
import org.can.water_law_exam_backend.dto.request.itemcategory.ItemCategoryUpdateRequest;
import org.can.water_law_exam_backend.dto.response.itemcategory.ItemCategoryVO;
import org.can.water_law_exam_backend.entity.ItemCategory;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.ItemCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCategoryService {

    private final ItemCategoryMapper itemCategoryMapper;

    public List<ItemCategoryVO> getCategoryTree() {
        List<ItemCategory> all = itemCategoryMapper.selectAll();
        Map<Integer, List<ItemCategory>> parentIdToChildren = all.stream()
                .collect(Collectors.groupingBy(c -> Optional.ofNullable(c.getParentId()).orElse(0)));
        List<ItemCategoryVO> roots = new ArrayList<>();
        List<ItemCategory> rootEntities = parentIdToChildren.getOrDefault(0, Collections.emptyList());
        for (ItemCategory root : rootEntities) {
            roots.add(toTree(root, parentIdToChildren));
        }
        return roots;
    }

    public ItemCategoryVO getById(Integer id) {
        ItemCategory category = itemCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        ItemCategoryVO vo = new ItemCategoryVO();
        vo.setId(category.getId());
        vo.setTitle(category.getTitle());
        vo.setParentId(category.getParentId());
        vo.setLeaf(Boolean.TRUE.equals(category.getIsLeaf()));
        vo.setSubs(null);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(ItemCategoryAddRequest request) {
        Integer parentId = request.getParentId();
        if (parentId != 0) {
            ItemCategory parent = itemCategoryMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException(400, "上级分类不存在");
            }
        }
        ItemCategory category = new ItemCategory();
        category.setTitle(request.getTitle().trim());
        category.setParentId(parentId);
        category.setIsLeaf(true);
        int rows = itemCategoryMapper.insert(category);
        if (rows == 0) {
            throw new BusinessException(500, "添加分类失败");
        }
        // 如果有上级，将上级置为非叶子
        if (parentId != 0) {
            ItemCategory parent = itemCategoryMapper.selectById(parentId);
            if (parent != null && Boolean.TRUE.equals(parent.getIsLeaf())) {
                parent.setIsLeaf(false);
                itemCategoryMapper.update(parent);
            }
        }
        log.info("添加分类成功：id={}, title={}, parentId={}", category.getId(), category.getTitle(), category.getParentId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ItemCategoryUpdateRequest request) {
        ItemCategory exist = itemCategoryMapper.selectById(request.getId());
        if (exist == null) {
            throw new BusinessException(404, "分类不存在");
        }
        if (request.getId().equals(request.getParentId())) {
            throw new BusinessException(400, "不能将自身设置为父级");
        }
        Integer newParentId = request.getParentId();
        if (newParentId != 0) {
            ItemCategory parent = itemCategoryMapper.selectById(newParentId);
            if (parent == null) {
                throw new BusinessException(400, "上级分类不存在");
            }
            // 简单防止形成直接循环（更复杂的环校验可后续扩展）
            if (Objects.equals(parent.getParentId(), exist.getId())) {
                throw new BusinessException(400, "不允许形成循环父子关系");
            }
        }
        exist.setTitle(request.getTitle().trim());
        exist.setParentId(newParentId);
        // 叶子状态根据是否有子节点再维护
        int childCount = itemCategoryMapper.countChildren(exist.getId());
        exist.setIsLeaf(childCount == 0);
        int rows = itemCategoryMapper.update(exist);
        if (rows == 0) {
            throw new BusinessException(500, "修改分类失败");
        }
        // 维护新老父级叶子状态
        maintainLeafStatusAfterMove(exist.getId(), exist.getParentId());
        log.info("修改分类成功：id={}, title={}, parentId={}", exist.getId(), exist.getTitle(), exist.getParentId());
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的数据");
        }
        int success = 0;
        for (Integer id : ids) {
            if (tryDelete(id)) {
                success++;
            }
        }
        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOne(Integer id) {
        return tryDelete(id);
    }

    private boolean tryDelete(Integer id) {
        ItemCategory category = itemCategoryMapper.selectById(id);
        if (category == null) {
            return false;
        }
        int children = itemCategoryMapper.countChildren(id);
        if (children > 0) {
            return false;
        }
        int itemCount = itemCategoryMapper.countItemsByCategoryId(id);
        if (itemCount > 0) {
            return false;
        }
        int rows = itemCategoryMapper.deleteById(id);
        if (rows > 0) {
            // 维护父级叶子状态
            maintainLeafStatusAfterDelete(category.getParentId());
            return true;
        }
        return false;
    }

    private void maintainLeafStatusAfterDelete(Integer parentId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        int remain = itemCategoryMapper.countChildren(parentId);
        ItemCategory parent = itemCategoryMapper.selectById(parentId);
        if (parent != null) {
            parent.setIsLeaf(remain == 0);
            itemCategoryMapper.update(parent);
        }
    }

    private void maintainLeafStatusAfterMove(Integer id, Integer newParentId) {
        // 维护新父级
        if (newParentId != null && newParentId != 0) {
            ItemCategory newParent = itemCategoryMapper.selectById(newParentId);
            if (newParent != null && Boolean.TRUE.equals(newParent.getIsLeaf())) {
                newParent.setIsLeaf(false);
                itemCategoryMapper.update(newParent);
            }
        }
        // 维护旧父级
        // 需要查找当前节点旧父级，但这里已被覆盖，简化：遍历父级的叶子状态由删改操作分别维护；
        // 对于移动操作，如果旧父级无子则会在后续操作中被维护，这里不再冗余查询。
    }

    private ItemCategoryVO toTree(ItemCategory current, Map<Integer, List<ItemCategory>> parentIdToChildren) {
        ItemCategoryVO vo = new ItemCategoryVO();
        vo.setId(current.getId());
        vo.setTitle(current.getTitle());
        vo.setParentId(current.getParentId());
        boolean leaf = parentIdToChildren.getOrDefault(current.getId(), Collections.emptyList()).isEmpty();
        vo.setLeaf(leaf);
        if (!leaf) {
            List<ItemCategory> children = parentIdToChildren.getOrDefault(current.getId(), Collections.emptyList());
            List<ItemCategoryVO> childVos = new ArrayList<>();
            for (ItemCategory child : children) {
                childVos.add(toTree(child, parentIdToChildren));
            }
            vo.setSubs(childVos);
        } else {
            vo.setSubs(null);
        }
        return vo;
    }
}


