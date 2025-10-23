package org.can.water_law_exam_backend.dto.request.accountuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 修改学员请求DTO
 */
@Data
public class AccountUserUpdateRequest {

    /**
     * 学员ID
     */
    @NotNull(message = "学员ID不能为空")
    private Long id;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 工作单位ID
     */
    @NotNull(message = "工作单位ID不能为空")
    private Long orgId;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$", 
             message = "身份证号格式不正确")
    private String idNo;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码（可选）
     */
    private String pwd;

    /**
     * 是否使用身份证号后6位作为初始密码
     */
    @NotNull(message = "last6digits参数不能为空")
    private Boolean last6digits;
}

