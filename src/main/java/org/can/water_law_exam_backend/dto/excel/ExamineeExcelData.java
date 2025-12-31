package org.can.water_law_exam_backend.dto.excel;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 考生Excel导入数据模型
 *
 * 建议Excel模板列（从左到右）：
 * 0-姓名   1-身份证号   2-手机号   3-单位名称   4-城市ID（可选）
 */
@Data
public class ExamineeExcelData {

    /**
     * 学员姓名
     */
    @ExcelProperty(index = 0)
    private String name;

    /**
     * 身份证号
     */
    @ExcelProperty(index = 1)
    private String idNo;

    /**
     * 手机号
     */
    @ExcelProperty(index = 2)
    private String phone;

    /**
     * 单位名称
     */
    @ExcelProperty(index = 3)
    private String orgName;

    /**
     * 城市ID（可选，用于在不同城市下区分同名单位）
     */
    @ExcelProperty(index = 4)
    private String cityId;
}


