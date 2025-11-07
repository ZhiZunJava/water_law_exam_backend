package org.can.water_law_exam_backend.dto.request.itembank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

/**
 * 题目分页查询请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemBankPageRequest extends PageRequest {

    /**
     * 查询参数
     */
    private ItemBankPageParam param;

    /**
     * 题目分页查询参数
     */
    @Data
    public static class ItemBankPageParam {
        /**
         * 题目类别ID（可选）
         */
        private Integer cId;

        /**
         * 题目类型ID（可选）
         */
        private Integer tId;

        /**
         * 题目关键字（可选）
         */
        private String key;
    }
}

