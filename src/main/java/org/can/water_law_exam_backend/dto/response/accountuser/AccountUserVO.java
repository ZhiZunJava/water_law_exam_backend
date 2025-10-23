package org.can.water_law_exam_backend.dto.response.accountuser;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员信息VO
 */
@Data
public class AccountUserVO {

    /**
     * 学员ID
     */
    private Long id;

    /**
     * 学员姓名
     */
    private String name;

    /**
     * 所属单位ID
     */
    private Long orgId;

    /**
     * 所属单位名称
     */
    private String orgName;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 是否禁用：false-正常，true-禁用
     */
    private Boolean locked;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 用户拥有的角色列表（可选）
     */
    private List<String> roles;
}

