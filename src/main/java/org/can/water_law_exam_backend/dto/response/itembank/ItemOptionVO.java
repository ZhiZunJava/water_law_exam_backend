package org.can.water_law_exam_backend.dto.response.itembank;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ItemOptionVO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;      // 对应选项内容
    private Boolean checked;   // 对应是否正确答案
}
