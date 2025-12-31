package org.can.water_law_exam_backend.dto.response.examinee;

import lombok.Data;

@Data
public class ExamineePageVO {
    private Long userId;
    private String userName;
    private String phone;
    private String org;
    private Long orgId;
    private Integer verifiedStatus;
    private String idNo;
}



