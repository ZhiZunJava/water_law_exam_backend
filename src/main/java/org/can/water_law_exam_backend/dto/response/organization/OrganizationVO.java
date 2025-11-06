package org.can.water_law_exam_backend.dto.response.organization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单位响应VO
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
public class OrganizationVO {

    /**
     * 单位ID
     */
    @JsonProperty("orgId")
    private Long id;

    /**
     * 单位名称
     */
    @JsonProperty("org")
    private String orgName;

    /**
     * 所属城市ID
     */
    private Integer cityId;

    /**
     * 所属城市名称
     */
    private String cityName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

