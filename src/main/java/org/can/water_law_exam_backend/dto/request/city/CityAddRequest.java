package org.can.water_law_exam_backend.dto.request.city;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 添加城市请求DTO
 */
@Data
public class CityAddRequest {

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空")
    private String city;
}

