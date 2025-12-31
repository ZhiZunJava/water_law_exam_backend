package org.can.water_law_exam_backend.dto.excel;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 成绩导出行模型
 */
@Data
public class ScoreExportRow {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("单位")
    private String org;

    @ExcelProperty("身份证号")
    private String idNo;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("总分")
    private Double totalScore;

    @ExcelProperty("是否及格")
    private String pass;

    @ExcelProperty("提交时间")
    private String submitTime;
}






































