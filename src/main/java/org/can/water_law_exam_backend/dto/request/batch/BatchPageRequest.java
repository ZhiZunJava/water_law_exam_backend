package org.can.water_law_exam_backend.dto.request.batch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class BatchPageRequest extends PageRequest {

    private Param param;

    @Data
    public static class Param {
        /**
         * 批次名称关键字
         */
        private String key;

        /**
         * 状态：null=全部；true=未启用；false=启用
         */
        private Boolean lock;
    }
}



