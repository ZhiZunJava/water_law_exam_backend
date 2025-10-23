package org.can.water_law_exam_backend.dto.request.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新城市请求DTO
 */
@Data
public class CityUpdateRequest {

    /**
     * 城市ID
     */
    @NotNull(message = "城市ID不能为空")
    private Integer cityId;

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空")
    private String city;
}

