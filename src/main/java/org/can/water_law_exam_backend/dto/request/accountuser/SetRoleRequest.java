package org.can.water_law_exam_backend.dto.request.accountuser;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设置用户角色请求DTO
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
public class SetRoleRequest {

    /**
     * 被授予角色的用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long uid;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Integer rid;
}



