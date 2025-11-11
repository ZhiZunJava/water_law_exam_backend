package org.can.water_law_exam_backend.dto.response.admin;

import lombok.Data;

@Data
public class AdminVO {
    private Long id;
    private String name;
    private String userNo;
    private Boolean enabled;
    private String pwd; // 始终返回null，由服务层置空
}


