package org.can.water_law_exam_backend.dto.request.examinee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamineeOptionalPageRequest extends PageRequest {

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
    }
}



