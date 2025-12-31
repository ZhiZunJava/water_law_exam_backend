package org.can.water_law_exam_backend.dto.request.examinee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamineePageRequest extends PageRequest {

    private Param param;

    @Data
    public static class Param {
        /**
         * 考试批次ID
         */
        @JsonProperty("bId")
        private Long bId;
        /**
         * 检索关键字：单位、姓名或身份证号
         */
        private String key;
        /**
         * 报名审核状态：0-未审核；1-审核通过；-1-审核不通过
         */
        private Integer status;
    }
}



