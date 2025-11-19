package org.can.water_law_exam_backend.dto.request.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplatePageRequest extends PageRequest {
    private Param param;

    @Data
    public static class Param {
        private String key;
        private Boolean detail; // 是否返回details
    }
}
