package org.can.water_law_exam_backend.dto.request.score;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScorePageRequest extends PageRequest {

    private Param param;

    @Data
    public static class Param {
        /**
         * 学员姓名关键字
         */
        private String key;

        /**
         * 分类标识：
         * 0-含未提交；1-已提交不及格+未提交；
         * 2-已提交（及格+不及格）；3-已提交不及格；4-已提交及格
         */
        private Integer c;
    }
}



