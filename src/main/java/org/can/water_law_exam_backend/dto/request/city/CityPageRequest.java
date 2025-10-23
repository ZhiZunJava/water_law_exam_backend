package org.can.water_law_exam_backend.dto.request.city;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.can.water_law_exam_backend.common.base.PageRequest;

/**
 * 城市分页查询请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CityPageRequest extends PageRequest {

    /**
     * 查询参数
     */
    private CityPageParam param;

    /**
     * 城市分页查询参数
     */
    @Data
    public static class CityPageParam {
        /**
         * 城市名称关键字（模糊查询）
         */
        private String key;
    }
}

