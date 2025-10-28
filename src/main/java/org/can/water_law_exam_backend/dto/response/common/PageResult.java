package org.can.water_law_exam_backend.dto.response.common;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 是否是第一页
     */
    private Boolean isFirstPage;

    /**
     * 是否是最后一页
     */
    private Boolean isLastPage;

    /**
     * 是否有上一页
     */
    private Boolean hasPreviousPage;

    /**
     * 是否有下一页
     */
    private Boolean hasNextPage;

    /**
     * 从PageInfo构造PageResult
     *
     * @param pageInfo PageHelper的PageInfo对象
     * @param <T>      数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(PageInfo<T> pageInfo) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(pageInfo.getList());
        result.setIsFirstPage(pageInfo.isIsFirstPage());
        result.setIsLastPage(pageInfo.isIsLastPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        return result;
    }

    /**
     * 从PageInfo构造PageResult（带数据转换）
     *
     * @param pageInfo PageHelper的PageInfo对象
     * @param list     转换后的数据列表
     * @param <T>      数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(PageInfo<?> pageInfo, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        result.setIsFirstPage(pageInfo.isIsFirstPage());
        result.setIsLastPage(pageInfo.isIsLastPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        return result;
    }

    /**
     * 简单构造函数（兼容旧代码）
     *
     * @param total 总记录数
     * @param list  数据列表
     */
    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}

