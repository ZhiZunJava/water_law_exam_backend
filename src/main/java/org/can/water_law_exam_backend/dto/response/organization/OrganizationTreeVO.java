package org.can.water_law_exam_backend.dto.response.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织树形结构响应VO
 * 用于统一返回城市和单位数据
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationTreeVO {

    /**
     * ID（城市ID或单位ID）
     */
    private Long id;

    /**
     * 名称（城市名称或单位名称）
     */
    private String name;

    /**
     * 类型：city-城市，organization-单位
     */
    private String type;

    /**
     * 父级ID（城市的父级为0，单位的父级为城市ID）
     */
    private Long parentId;

    /**
     * 城市ID（仅单位有）
     */
    private Integer cityId;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 从城市对象构建
     */
    public static OrganizationTreeVO fromCity(org.can.water_law_exam_backend.entity.City city) {
        OrganizationTreeVO vo = new OrganizationTreeVO();
        vo.setId(city.getId().longValue());
        vo.setName(city.getCityName());
        vo.setType("city");
        vo.setParentId(0L);
        vo.setEnabled(true);
        return vo;
    }

    /**
     * 从单位对象构建
     */
    public static OrganizationTreeVO fromOrganization(org.can.water_law_exam_backend.entity.Organization org) {
        OrganizationTreeVO vo = new OrganizationTreeVO();
        vo.setId(org.getId());
        vo.setName(org.getOrgName());
        vo.setType("organization");
        vo.setParentId(org.getCityId().longValue());
        vo.setCityId(org.getCityId());
        vo.setEnabled(true);
        return vo;
    }
}

