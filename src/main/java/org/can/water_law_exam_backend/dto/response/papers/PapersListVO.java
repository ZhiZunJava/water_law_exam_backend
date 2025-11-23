package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

@Data
public class PapersListVO {
    private Long id;
    private String title;
    private Integer no;
    private String creator;
    private String createTime; // 格式化为字符串
}
