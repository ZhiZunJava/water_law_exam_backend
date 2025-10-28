package org.can.water_law_exam_backend.dto.request.organization;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

/**
 * 单位分页查询请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationPageRequest extends PageRequest {

    /**
     * 查询参数
     */
    private OrganizationPageParam param;

    /**
     * 单位分页查询参数
     */
    @Data
    public static class OrganizationPageParam {
        /**
         * 城市ID（无此参数则不区分城市）
         */
        private Integer cId;
        
        /**
         * 单位名称关键字（模糊查询）
         */
        private String key;
    }
}

