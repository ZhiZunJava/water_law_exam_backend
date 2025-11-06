package org.can.water_law_exam_backend.dto.request.accountuser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

/**
 * 学员分页查询请求DTO
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUserPageRequest extends PageRequest {

    /**
     * 查询参数
     */
    private Param param;

    /**
     * 查询参数内部类
     */
    @Data
    public static class Param {
        /**
         * 单位ID（可选）
         */
        private Long org;

        /**
         * 检索关键字（可选，按姓名或身份证号模糊查询）
         */
        private String key;
    }
}



