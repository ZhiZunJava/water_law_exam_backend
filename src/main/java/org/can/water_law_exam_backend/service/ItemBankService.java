package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.excel.ItemBankExcelData;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankAddRequest;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankPageRequest;
import org.can.water_law_exam_backend.dto.request.itembank.ItemBankUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.itembank.ItemBankVO;
import org.can.water_law_exam_backend.dto.response.itembank.ItemOptionVO;
import org.can.water_law_exam_backend.entity.ItemBank;
import org.can.water_law_exam_backend.entity.ItemOption;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.ItemBankMapper;
import org.can.water_law_exam_backend.mapper.ItemOptionMapper;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 题库服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemBankService {

    private final ItemBankMapper itemBankMapper;
    private final ItemOptionMapper itemOptionMapper;

    /**
     * 导入题库（从Excel文件）
     * Excel格式：题型 | 题干 | 选项A | 选项B | 选项C | 选项D | 答案 | 答案解析 | 题目分类ID | 重点题目
     * 
     * @param file Excel 文件
     */
    @Transactional(rollbackFor = Exception.class)
    public int importItemBank(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(1, "上传文件不能为空");
        }

        // 检查文件格式
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new BusinessException(1, "只支持Excel 2007版本以后格式文件(.xlsx)");
        }

        try (InputStream is = file.getInputStream()) {
            // 创建监听器来处理每一行数据
            ItemBankImportListener listener = new ItemBankImportListener();
            
            // 使用 FastExcel读取Excel，指定读取Sheet1
            FastExcel.read(is, ItemBankExcelData.class, listener)
                    .sheet("导入题目模板")
                    .doRead();
            
            // 获取导入结果
            int successCount = listener.getSuccessCount();
            int failCount = listener.getFailCount();
            
            log.info("题库导入完成：文件名={}，成功{}条，失败{}条", filename, successCount, failCount);
            
            if (successCount == 0) {
                throw new BusinessException(1, "导入失败：没有成功导入任何题目");
            }

            return successCount;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入题库失败：{}", e.getMessage(), e);
            throw new BusinessException(1, "导入失败：" + e.getMessage());
        }
    }
    
    /**
     * Excel 导入监听器
     */
    @Getter
    private class ItemBankImportListener implements ReadListener<ItemBankExcelData> {
        
        private int successCount = 0;
        private int failCount = 0;
        
        @Override
        public void invoke(ItemBankExcelData data, AnalysisContext context) {
            try {
                // 验证必填字段
                if (isEmpty(data.getTypeName()) || isEmpty(data.getContent()) || 
                    isEmpty(data.getAnswer()) || isEmpty(data.getCategoryId())) {
                    log.warn("行{}：必填字段缺失，跳过", context.readRowHolder().getRowIndex() + 1);
                    failCount++;
                    return;
                }
                
                // 解析题型 ID
                Integer typeId = parseTypeId(data.getTypeName());
                if (typeId == null) {
                    log.warn("行{}：不支持的题型'{}'，跳过", context.readRowHolder().getRowIndex() + 1, data.getTypeName());
                    failCount++;
                    return;
                }
                
                // 解析分类 ID
                int categoryId;
                try {
                    categoryId = Integer.parseInt(data.getCategoryId().trim());
                } catch (NumberFormatException e) {
                    log.warn("行{}：分类ID格式错误'{}'，跳过", context.readRowHolder().getRowIndex() + 1, data.getCategoryId());
                    failCount++;
                    return;
                }
                
                // 创建题目
                ItemBank itemBank = new ItemBank();
                itemBank.setTypeId(typeId);
                itemBank.setCategoryId(categoryId);
                itemBank.setContent(data.getContent().trim());
                // 解析是否重点题目：支持 "1"/"true" 表示是，"0"/"false" 表示否
                boolean isKeyItem;
                try {
                    String keyItemStr = data.getIsKeyItem().trim();
                    isKeyItem = "1".equals(keyItemStr) || "true".equalsIgnoreCase(keyItemStr);
                } catch (RuntimeException e) {
                    isKeyItem = false;
                }
                itemBank.setIsKeyItem(isKeyItem);
                itemBank.setExplanation(data.getExplanation() != null ? data.getExplanation().trim() : "");
                
                // 插入题目
                itemBankMapper.insert(itemBank);
                
                // 处理选项
                List<ItemOption> options = buildOptionsFromExcel(
                    itemBank.getId(), typeId,
                    data.getOptionA(), data.getOptionB(),
                    data.getOptionC(), data.getOptionD(),
                    data.getOptionE(), data.getOptionF(),
                    data.getOptionG(), data.getOptionH(),
                    data.getAnswer()
                );
                
                if (!options.isEmpty()) {
                    itemOptionMapper.insertBatch(options);
                }
                
                successCount++;
                
            } catch (Exception e) {
                log.error("行{}：导入失败，错误：{}", context.readRowHolder().getRowIndex() + 1, e.getMessage());
                failCount++;
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("所有数据解析完成");
        }

        private boolean isEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
    }
    
    /**
     * 根据题型名称解析题型ID
     */
    private Integer parseTypeId(String typeName) {
        typeName = typeName.trim();
        return switch (typeName) {
            case "单选", "单选题" -> 1;
            case "多选", "多选题" -> 2;
            case "判断", "判断题" -> 3;
            default -> null;
        };
    }
    
    /**
     * 从Excel数据构建选项列表
     */
    private List<ItemOption> buildOptionsFromExcel(Long itemId, Integer typeId,
                                                   String optionA, String optionB,
                                                   String optionC, String optionD,
                                                   String optionE, String optionF,
                                                   String optionG, String optionH,
                                                   String answer) {
        List<ItemOption> options = new ArrayList<>();
        
        // 判断题
        if (typeId == 3) {
            // 答案应为"正确"或"错误"/"对"或"错"/A或B
            boolean isCorrect;

            // 判断答案是哪个选项
            if (answer.equalsIgnoreCase("A") || answer.contains("选项A")) {
                isCorrect = true;  // A 是正确答案
            } else if (answer.equalsIgnoreCase("B") || answer.contains("选项B")) {
                isCorrect = false; // B 是正确答案
            } else {
                // 传统方式：根据"正确"、"对"等关键词判断
                isCorrect = answer.contains("正确") || answer.contains("对") ||
                          answer.equalsIgnoreCase("T") || answer.equalsIgnoreCase("TRUE");
            }

            // 如果A、B选项有值，优先使用
            String option1Title = (optionA != null && !optionA.trim().isEmpty()) ? optionA.trim() : "正确";
            String option2Title = (optionB != null && !optionB.trim().isEmpty()) ? optionB.trim() : "错误";
            
            ItemOption option1 = new ItemOption();
            option1.setItemId(itemId);
            option1.setOptionNo(1);
            option1.setOptionTitle(option1Title);
            option1.setIsCorrect(isCorrect);
            options.add(option1);
            
            ItemOption option2 = new ItemOption();
            option2.setItemId(itemId);
            option2.setOptionNo(2);
            option2.setOptionTitle(option2Title);
            option2.setIsCorrect(!isCorrect);
            options.add(option2);
            
        } else {
            // 单选题/多选题
            // 答案格式：A、B、AB、ABC等
            String normalizedAnswer = answer.toUpperCase().trim();
            
            // 用于记录哪些选项字母是有效的（有内容的）
            java.util.Set<String> validOptions = new java.util.HashSet<>();
            
            if (optionA != null && !optionA.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(1);
                option.setOptionTitle(optionA.trim());
                option.setIsCorrect(normalizedAnswer.contains("A"));
                options.add(option);
                validOptions.add("A");
            }
            
            if (optionB != null && !optionB.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(2);
                option.setOptionTitle(optionB.trim());
                option.setIsCorrect(normalizedAnswer.contains("B"));
                options.add(option);
                validOptions.add("B");
            }
            
            if (optionC != null && !optionC.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(3);
                option.setOptionTitle(optionC.trim());
                option.setIsCorrect(normalizedAnswer.contains("C"));
                options.add(option);
                validOptions.add("C");
            }
            
            if (optionD != null && !optionD.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(4);
                option.setOptionTitle(optionD.trim());
                option.setIsCorrect(normalizedAnswer.contains("D"));
                options.add(option);
                validOptions.add("D");
            }
            
            if (optionE != null && !optionE.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(5);
                option.setOptionTitle(optionE.trim());
                option.setIsCorrect(normalizedAnswer.contains("E"));
                options.add(option);
                validOptions.add("E");
            }
            
            if (optionF != null && !optionF.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(6);
                option.setOptionTitle(optionF.trim());
                option.setIsCorrect(normalizedAnswer.contains("F"));
                options.add(option);
                validOptions.add("F");
            }
            
            if (optionG != null && !optionG.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(7);
                option.setOptionTitle(optionG.trim());
                option.setIsCorrect(normalizedAnswer.contains("G"));
                options.add(option);
                validOptions.add("G");
            }
            
            if (optionH != null && !optionH.trim().isEmpty()) {
                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(8);
                option.setOptionTitle(optionH.trim());
                option.setIsCorrect(normalizedAnswer.contains("H"));
                options.add(option);
                validOptions.add("H");
            }
            
            // 验证：单选/多选题至少需要2个选项
            if (options.isEmpty()) {
                throw new BusinessException(1, "单选题/多选题至少需要提供2个选项");
            }
            if (options.size() < 2) {
                throw new BusinessException(1, "单选题/多选题至少需要2个选项，当前只有" + options.size() + "个");
            }
            
            // 验证：答案中的选项字母必须在实际选项中存在
            for (char c : normalizedAnswer.toCharArray()) {
                if (c >= 'A' && c <= 'Z') {
                    String optionLetter = String.valueOf(c);
                    if (!validOptions.contains(optionLetter)) {
                        throw new BusinessException(1, "答案中的选项" + optionLetter + "不存在，请检查选项内容");
                    }
                }
            }
            
            // 验证：至少要有一个正确答案
            boolean hasCorrectAnswer = options.stream().anyMatch(ItemOption::getIsCorrect);
            if (!hasCorrectAnswer) {
                throw new BusinessException(1, "未找到正确答案，请检查答案列是否正确（如：A、B、AB等）");
            }
        }
        
        return options;
    }

    /**
     * 添加试题
     *
     * @param request 添加请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void addItemBank(ItemBankAddRequest request) {
        // 创建题目对象
        ItemBank itemBank = new ItemBank();
        itemBank.setTypeId(request.getTypeId());
        itemBank.setCategoryId(request.getCategoryId());
        itemBank.setContent(request.getContent());
        itemBank.setExplanation(request.getExplain());
        itemBank.setIsKeyItem(request.getIsKeyItem() != null && request.getIsKeyItem());

        // 插入题目
        int rows = itemBankMapper.insert(itemBank);
        if (rows == 0) {
            throw new BusinessException(1, "添加题目失败");
        }

        // 处理选项
        List<ItemOption> options = processOptions(itemBank.getId(), request.getTypeId(), request.getOptions());

        if (!options.isEmpty()) {
            itemOptionMapper.insertBatch(options);
        }

        log.info("添加题目成功：id={}, typeId={}, categoryId={}", 
                itemBank.getId(), itemBank.getTypeId(), itemBank.getCategoryId());
    }

    /**
     * 验证选项标题是否为空
     * 
     * @param title 选项标题
     * @throws BusinessException 如果标题为空
     */
    private void validateOptionTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException(1, "单选题/多选题的选项内容不能为空");
        }
    }

    /**
     * 处理选项数据
     * 如果是判断题（typeId=3），自动添加"正确"和"错误"选项
     * 
     * @param itemId 题目ID
     * @param typeId 题型ID
     * @param optionDTOs 前端传入的选项列表
     * @return 处理后的选项列表
     */
    private List<ItemOption> processOptions(Long itemId, Integer typeId, 
                                           List<ItemBankAddRequest.OptionDTO> optionDTOs) {
        List<ItemOption> options = new java.util.ArrayList<>();
        
        // 验证选项列表
        if (optionDTOs == null || optionDTOs.isEmpty()) {
            throw new BusinessException(1, "选项列表不能为空");
        }
        
        // 判断题特殊处理（typeId=3）
        if (typeId == 3) {
            // 判断题固定两个选项："正确"和"错误"
            // 前端只传 [{"checked": true}] 或 [{"checked": false}]
            boolean correctAnswer = optionDTOs.get(0).getChecked();
            
            // 选项1：正确
            ItemOption option1 = new ItemOption();
            option1.setItemId(itemId);
            option1.setOptionNo(1);
            option1.setOptionTitle("正确");
            option1.setIsCorrect(correctAnswer);
            options.add(option1);
            
            // 选项2：错误
            ItemOption option2 = new ItemOption();
            option2.setItemId(itemId);
            option2.setOptionNo(2);
            option2.setOptionTitle("错误");
            option2.setIsCorrect(!correctAnswer);
            options.add(option2);
        } else {
            // 单选题/多选题：使用前端传入的选项
            int optionNo = 1;
            for (ItemBankAddRequest.OptionDTO dto : optionDTOs) {
                // 验证选项标题
                validateOptionTitle(dto.getTitle());

                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(dto.getOptionNo() != null ? dto.getOptionNo() : optionNo++);
                option.setOptionTitle(dto.getTitle().trim());
                option.setIsCorrect(dto.getChecked());
                options.add(option);
            }
        }
        
        return options;
    }

    /**
     * 处理选项数据（更新时使用）
     * 如果是判断题（typeId=3），自动添加"正确"和"错误"选项
     * 
     * @param itemId 题目ID
     * @param typeId 题型ID
     * @param optionDTOs 前端传入的选项列表
     * @return 处理后的选项列表
     */
    private List<ItemOption> processOptionsForUpdate(Long itemId, Integer typeId, 
                                                     List<ItemBankUpdateRequest.OptionDTO> optionDTOs) {
        List<ItemOption> options = new java.util.ArrayList<>();
        
        // 验证选项列表
        if (optionDTOs == null || optionDTOs.isEmpty()) {
            throw new BusinessException(1, "选项列表不能为空");
        }
        
        // 判断题特殊处理（typeId=3）
        if (typeId == 3) {
            // 判断题固定两个选项："正确"和"错误"
            // 前端只传 [{"checked": true}] 或 [{"checked": false}]
            boolean correctAnswer = optionDTOs.get(0).getChecked();
            
            // 选项1：正确
            ItemOption option1 = new ItemOption();
            option1.setItemId(itemId);
            option1.setOptionNo(1);
            option1.setOptionTitle("正确");
            option1.setIsCorrect(correctAnswer);
            options.add(option1);
            
            // 选项2：错误
            ItemOption option2 = new ItemOption();
            option2.setItemId(itemId);
            option2.setOptionNo(2);
            option2.setOptionTitle("错误");
            option2.setIsCorrect(!correctAnswer);
            options.add(option2);
        } else {
            // 单选题/多选题：使用前端传入的选项
            int optionNo = 1;
            for (ItemBankUpdateRequest.OptionDTO dto : optionDTOs) {
                // 验证选项标题
                validateOptionTitle(dto.getTitle());

                ItemOption option = new ItemOption();
                option.setItemId(itemId);
                option.setOptionNo(dto.getOptionNo() != null ? dto.getOptionNo() : optionNo++);
                option.setOptionTitle(dto.getTitle().trim());
                option.setIsCorrect(dto.getChecked());
                options.add(option);
            }
        }
        
        return options;
    }

    /**
     * 修改试题
     *
     * @param request 修改请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateItemBank(ItemBankUpdateRequest request) {
        // 检查题目是否存在
        ItemBank existingItem = itemBankMapper.selectById(request.getId());
        if (existingItem == null) {
            throw new BusinessException(1, "题目不存在");
        }

        // 更新题目
        ItemBank itemBank = new ItemBank();
        itemBank.setId(request.getId());
        itemBank.setTypeId(request.getTypeId());
        itemBank.setCategoryId(request.getCategoryId());
        itemBank.setIsKeyItem(request.getIsKeyItem() != null && request.getIsKeyItem());
        itemBank.setContent(request.getContent());
        itemBank.setExplanation(request.getExplain());

        int rows = itemBankMapper.update(itemBank);
        if (rows == 0) {
            throw new BusinessException(1, "修改题目失败");
        }

        // 删除旧选项
        itemOptionMapper.deleteByItemId(request.getId());

        // 处理选项
        List<ItemOption> options = processOptionsForUpdate(request.getId(), request.getTypeId(), request.getOptions());

        if (!options.isEmpty()) {
            itemOptionMapper.insertBatch(options);
        }

        log.info("修改题目成功：id={}", request.getId());
    }

    /**
     * 删除试题（批量）
     *
     * @param ids 题目ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteItemBanks(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(1, "请选择要删除的题目");
        }

        // 删除题目的选项
        for (Long id : ids) {
            itemOptionMapper.deleteByItemId(id);
        }

        // 删除题目
        int rows = itemBankMapper.deleteBatch(ids);
        if (rows == 0) {
            throw new BusinessException(1, "删除题目失败");
        }

        log.info("批量删除题目成功：删除数量={}", rows);
    }

    /**
     * 获取单条题目
     *
     * @param id 题目ID
     * @return 题目信息（包含选项）
     */
    public ItemBankVO getItemBankById(Long id) {
        ItemBank itemBank = itemBankMapper.selectById(id);
        if (itemBank == null) {
            throw new BusinessException(1, "题目不存在");
        }

        // 查询选项
        List<ItemOption> options = itemOptionMapper.selectByItemId(id);

        List<ItemOptionVO>  optionVOS = new ArrayList<>();
        Integer typeId = itemBank.getTypeId();
        if ( typeId == 3 ) {
            Boolean isCorrect = options.stream().anyMatch(ItemOption::getIsCorrect);
            ItemOptionVO correctOptionVO = new ItemOptionVO();
            correctOptionVO.setChecked(isCorrect);
            optionVOS.add(correctOptionVO);
        } else {
            for  (ItemOption option : options) {
                ItemOptionVO optionVO = new ItemOptionVO();
                optionVO.setTitle(option.getOptionTitle());
                optionVO.setChecked(option.getIsCorrect());
                optionVOS.add(optionVO);
            }
        }

        ItemBankVO itemBankVO = new ItemBankVO();
        BeanUtils.copyProperties(itemBank, itemBankVO);
        itemBankVO.setOptions(optionVOS);

        return itemBankVO;
    }

    /**
     * 分页查询题目列表
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    public PageResult<ItemBankVO> getItemBanksByPage(ItemBankPageRequest request) {
        // 获取查询参数
        Integer categoryId = null;
        Integer typeId = null;
        String key = null;
        if (request.getParam() != null) {
            categoryId = request.getParam().getCId();
            typeId = request.getParam().getTId();

            if (request.getParam().getKey() != null) {
                key = request.getParam().getKey().trim();
                key = key.isEmpty() ? null : key; // 简化空字符串处理
            }
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(request.getPage(), request.getSize());
        List<ItemBank> itemBankList = itemBankMapper.selectByPage(categoryId, typeId, key);

        // 转换为VO列表并处理选项
        List<ItemBankVO> voList = new ArrayList<>();
        for (ItemBank itemBank : itemBankList) {
            // 1. 基础属性拷贝
            ItemBankVO itemBankVO = new ItemBankVO();
            BeanUtils.copyProperties(itemBank, itemBankVO);

            // 2. 处理选项
            List<ItemOption> options = itemOptionMapper.selectByItemId(itemBank.getId());
            List<ItemOptionVO> optionVOS = new ArrayList<>();

            if (itemBank.getTypeId() == 3) { // 特定题型处理
                boolean hasCorrect = options.stream().anyMatch(ItemOption::getIsCorrect);
                ItemOptionVO correctVO = new ItemOptionVO();
                correctVO.setChecked(hasCorrect);
                optionVOS.add(correctVO);
            } else { // 普通题型处理
                for (ItemOption option : options) {
                    ItemOptionVO optionVO = new ItemOptionVO();
                    optionVO.setTitle(option.getOptionTitle());
                    optionVO.setChecked(option.getIsCorrect());
                    optionVOS.add(optionVO);
                }
            }

            itemBankVO.setOptions(optionVOS);
            voList.add(itemBankVO);
        }

        // 构建分页结果（基于原始分页信息转换）
        PageInfo<ItemBank> originalPageInfo = new PageInfo<>(itemBankList);
        PageInfo<ItemBankVO> voPageInfo = new PageInfo<>(voList);
        voPageInfo.setTotal(originalPageInfo.getTotal()); // 复用总条数
        voPageInfo.setPages(originalPageInfo.getPages()); // 复用总页数

        return PageResult.of(voPageInfo);
    }
}

