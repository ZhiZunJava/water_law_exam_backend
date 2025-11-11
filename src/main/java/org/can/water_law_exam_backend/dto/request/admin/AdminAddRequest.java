package org.can.water_law_exam_backend.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminAddRequest {
    @NotBlank(message = "账号昵称不能为空")
    @Pattern(regexp = "^\\S.{3,14}\\S$|^\\S{5,16}$", message = "账号昵称长度需为5-16位非空字符")
    private String name;

    @NotBlank(message = "用户账号不能为空")
    private String userNo;

    /**
     * 可选密码。若非空需满足：6-20位，包含数字、大小写字母、特殊符号四类
     */
    private String pwd;
}
