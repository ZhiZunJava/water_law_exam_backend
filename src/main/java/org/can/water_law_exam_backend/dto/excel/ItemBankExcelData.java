package org.can.water_law_exam_backend.dto.excel;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 题库Excel导入数据模型
 */
@Data
public class ItemBankExcelData {
    
    /**
     * 题型：单选、多选、判断
     */
    @ExcelProperty(index = 0)
    private String typeName;
    
    /**
     * 题干
     */
    @ExcelProperty(index = 1)
    private String content;
    
    /**
     * 选项A
     */
    @ExcelProperty(index = 2)
    private String optionA;
    
    /**
     * 选项B
     */
    @ExcelProperty(index = 3)
    private String optionB;
    
    /**
     * 选项C
     */
    @ExcelProperty(index = 4)
    private String optionC;
    
    /**
     * 选项D
     */
    @ExcelProperty(index = 5)
    private String optionD;
    
    /**
     * 选项E（可选）
     */
    @ExcelProperty(index = 6)
    private String optionE;
    
    /**
     * 选项F（可选）
     */
    @ExcelProperty(index = 7)
    private String optionF;
    
    /**
     * 选项G（可选）
     */
    @ExcelProperty(index = 8)
    private String optionG;
    
    /**
     * 选项H（可选）
     */
    @ExcelProperty(index = 9)
    private String optionH;
    
    /**
     * 答案：单选/多选为A、B、AB等；判断题为"正确"或"错误"
     */
    @ExcelProperty(index = 10)
    private String answer;
    
    /**
     * 答案解析
     */
    @ExcelProperty(index = 11)
    private String explanation;
    
    /**
     * 题目分类ID
     */
    @ExcelProperty(index = 12)
    private String categoryId;
}

