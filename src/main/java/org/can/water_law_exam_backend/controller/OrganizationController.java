package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationAddRequest;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationPageRequest;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.organization.OrganizationTreeVO;
import org.can.water_law_exam_backend.entity.Organization;
import org.can.water_law_exam_backend.service.OrganizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单位管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * 逐级获取下一级组织列表
     * 当 pId 为 0 或 null 时，返回所有城市（地级市）
     * 当 pId 为城市ID时，返回该城市下的所有单位
     *
     * @param pId     上级组织ID（0或null表示获取所有城市）
     * @param enabled 是否启用（仅对单位有效）
     * @return 下一级组织列表
     */
    @GetMapping("/list")
    public Result<List<OrganizationTreeVO>> getOrganizationsByParentId(
            @RequestParam(required = false) Long pId,
            @RequestParam(required = false) Boolean enabled) {
        log.info("逐级获取下一级组织列表：pId={}, enabled={}", pId, enabled);
        List<OrganizationTreeVO> organizations = organizationService.getOrganizationsByParentId(pId, enabled);
        return Result.success(organizations);
    }

    /**
     * 获取指定城市单位列表
     *
     * @param cityId 城市ID
     * @return 单位列表
     */
    @GetMapping("/list/{city_id}")
    public Result<List<Organization>> getOrganizationsByCityId(@PathVariable("city_id") Integer cityId) {
        log.info("获取指定城市单位列表：cityId={}", cityId);
        List<Organization> organizations = organizationService.getOrganizationsByCityId(cityId);
        return Result.success(organizations);
    }

    /**
     * 分页查询单位列表
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    @PostMapping("/pages")
    public Result<PageResult<Organization>> getOrganizationsByPage(@RequestBody OrganizationPageRequest request) {

        // 参数校验
        if (request.getPage() == null || request.getPage() < 1) {
            return Result.error(400, "页码必须大于0");
        }
        if (request.getSize() == null || request.getSize() < 1 || request.getSize() > 200) {
            return Result.error(400, "页大小必须在1-200之间");
        }

        log.info("分页查询单位列表：page={}, size={}, total={}, param={}", 
                request.getPage(), request.getSize(), request.getTotal(), request.getParam());

        PageResult<Organization> result = organizationService.getOrganizationsByPage(request);
        return Result.success(result);
    }

    /**
     * 获取单个单位详情
     *
     * @param id 单位ID
     * @return 单位信息
     */
    @GetMapping("/{id}")
    public Result<Organization> getOrganizationById(@PathVariable Long id) {
        log.info("获取单位详情：id={}", id);
        Organization organization = organizationService.getOrganizationById(id);
        return Result.success(organization);
    }

    /**
     * 添加单位
     *
     * @param request 添加请求
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Organization> addOrganization(@Valid @RequestBody OrganizationAddRequest request) {
        log.info("添加单位：org={}, cityId={}", request.getOrg(), request.getCityId());
        Organization organization = organizationService.addOrganization(request);
        return Result.success("成功添加单位数据", organization);
    }

    /**
     * 修改单位
     *
     * @param request 更新请求
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<Void> updateOrganization(@Valid @RequestBody OrganizationUpdateRequest request) {
        log.info("修改单位：orgId={}, org={}, cityId={}", request.getOrgId(), request.getOrg(), request.getCityId());
        organizationService.updateOrganization(request);
        return Result.success("成功修改单位数据", null);
    }

    /**
     * 单个删除单位
     *
     * @param id 单位ID
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteOrganization(@PathVariable Long id) {
        log.info("删除单位：id={}", id);
        organizationService.deleteOrganization(id);
        return Result.success("成功删除单位数据", null);
    }

    /**
     * 批量删除单位
     *
     * @param ids 单位ID数组
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<Void> deleteOrganizationsBatch(@RequestBody List<Long> ids) {
        log.info("批量删除单位：ids={}", ids);
        int count = organizationService.deleteOrganizationsBatch(ids);
        return Result.success("成功删除 " + count + " 条单位数据", null);
    }
}

