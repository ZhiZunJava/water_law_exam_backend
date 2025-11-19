package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

@Data
public class PapersContentOptionVO {
    private String title;
    private Boolean checked; // 正确为 true，非正确为 null
}
