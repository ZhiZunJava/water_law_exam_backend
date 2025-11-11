package org.can.water_law_exam_backend.dto.request.itemcategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCategoryUpdateRequest {

    /**
     * 分类ID
     */
    @NotNull(message = "id不能为空")
    private Integer id;

    /**
     * 上级分类ID（顶级为0）
     */
    @NotNull(message = "parentId不能为空")
    @Min(value = 0, message = "parentId不能小于0")
    private Integer parentId;

    /**
     * 分类标题
     */
    @NotBlank(message = "分类标题不能为空")
    private String title;
}


