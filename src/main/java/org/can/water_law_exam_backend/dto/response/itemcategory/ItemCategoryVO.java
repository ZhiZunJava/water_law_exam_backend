package org.can.water_law_exam_backend.dto.response.itemcategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemCategoryVO {

    private Integer id;

    private String title;

    private Integer parentId;

    private Boolean leaf;

    private List<ItemCategoryVO> subs;
}


