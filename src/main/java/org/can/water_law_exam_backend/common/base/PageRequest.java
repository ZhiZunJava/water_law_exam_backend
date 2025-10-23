package org.can.water_law_exam_backend.common.base;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 分页请求基类
 */
@Data
public class PageRequest {

    /**
     * 页码（从1开始）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page;

    /**
     * 每页大小
     */
    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小必须大于0")
    @Max(value = 200, message = "页大小不能超过200")
    private Integer size;

    /**
     * 总数据条数（可选）
     */
    private Integer total;

    /**
     * 计算偏移量
     *
     * @return 偏移量
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }
}

