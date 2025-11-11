package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestStruct {
    private Integer id;
    private Integer typeId;
    private String typeRemarks;
    private Integer score;
    private Integer totality;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非表字段
    private transient String typeName;
}


