package org.can.water_law_exam_backend.dto.request.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加单位请求DTO
 */
@Data
public class OrganizationAddRequest {

    /**
     * 单位名称
     */
    @NotBlank(message = "单位名称不能为空")
    private String org;

    /**
     * 所属城市ID
     */
    @NotNull(message = "所属城市ID不能为空")
    private Integer cityId;
}

