package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationAddRequest;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationPageRequest;
import org.can.water_law_exam_backend.dto.request.organization.OrganizationUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.organization.OrganizationTreeVO;
import org.can.water_law_exam_backend.entity.City;
import org.can.water_law_exam_backend.entity.Organization;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.CityMapper;
import org.can.water_law_exam_backend.mapper.OrganizationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单位服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationMapper organizationMapper;
    private final CityMapper cityMapper;

    /**
     * 逐级获取下一级组织列表
     * 当 pId 为 0 时，返回所有城市（地级市）
     * 当 pId 为城市ID时，返回该城市下的所有单位
     *
     * @param pId     上级组织ID（0表示获取所有城市）
     * @param enabled 是否启用（仅对单位有效）
     * @return 组织树形结构列表
     */
    public List<OrganizationTreeVO> getOrganizationsByParentId(Long pId, Boolean enabled) {
        List<OrganizationTreeVO> result = new ArrayList<>();

        // 当 pId 为 null、0 或负数时，返回所有城市
        if (pId == null || pId == 0) {
            // 获取所有城市（地级市）
            List<City> cities = cityMapper.selectAll();
            result = cities.stream()
                    .map(OrganizationTreeVO::fromCity)
                    .collect(Collectors.toList());
            log.info("获取所有城市，共{}个", result.size());
        } else {
            // 获取指定城市下的所有单位
            List<Organization> organizations = organizationMapper.selectByCityIdAndEnabled(pId.intValue(), enabled);
            result = organizations.stream()
                    .map(OrganizationTreeVO::fromOrganization)
                    .collect(Collectors.toList());
            log.info("获取城市ID={}下的单位，共{}个", pId, result.size());
        }

        return result;
    }

    /**
     * 根据城市ID获取单位列表
     *
     * @param cityId 城市ID
     * @return 单位列表
     */
    public List<Organization> getOrganizationsByCityId(Integer cityId) {
        // 检查城市是否存在
        City city = cityMapper.selectById(cityId);
        if (city == null) {
            throw new BusinessException(404, "城市不存在");
        }
        return organizationMapper.selectByCityId(cityId);
    }

    /**
     * 分页查询单位列表
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    public PageResult<Organization> getOrganizationsByPage(OrganizationPageRequest request) {
        // 获取查询参数
        String key = null;
        Integer cId = null;
        if (request.getParam() != null) {
            // 城市ID
            cId = request.getParam().getCId();
            
            // 单位名称关键字
            if (request.getParam().getKey() != null) {
                key = request.getParam().getKey().trim();
                if (key.isEmpty()) {
                    key = null;
                }
            }
        }

        PageHelper.startPage(request.getPage(), request.getSize());
        List<Organization> list = organizationMapper.selectByPage(cId, key);
        PageInfo<Organization> pageInfo = new PageInfo<>(list);

        return PageResult.of(pageInfo);
    }

    /**
     * 根据ID获取单位详情
     *
     * @param id 单位ID
     * @return 单位信息
     */
    public Organization getOrganizationById(Long id) {
        Organization organization = organizationMapper.selectById(id);
        if (organization == null) {
            throw new BusinessException(404, "单位不存在");
        }
        return organization;
    }

    /**
     * 添加单位
     *
     * @param request 添加请求
     * @return 新增的单位信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Organization addOrganization(OrganizationAddRequest request) {
        String orgName = request.getOrg().trim();
        Integer cityId = request.getCityId();

        // 检查城市是否存在
        City city = cityMapper.selectById(cityId);
        if (city == null) {
            throw new BusinessException(404, "所属城市不存在");
        }

        // 检查单位名称在该城市下是否已存在
        Organization existingOrg = organizationMapper.selectByNameAndCityId(orgName, cityId);
        if (existingOrg != null) {
            throw new BusinessException(400, "该城市下已存在同名单位");
        }

        // 创建单位对象
        Organization organization = new Organization();
        organization.setOrgName(orgName);
        organization.setCityId(cityId);

        // 插入数据库
        int rows = organizationMapper.insert(organization);
        if (rows == 0) {
            throw new BusinessException(500, "添加单位失败");
        }

        log.info("添加单位成功：{}, 所属城市：{}", orgName, city.getCityName());

        // 查询并返回完整信息（包含城市名称）
        return organizationMapper.selectById(organization.getId());
    }

    /**
     * 更新单位信息
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganization(OrganizationUpdateRequest request) {
        // 检查单位是否存在
        Organization organization = organizationMapper.selectById(request.getOrgId());
        if (organization == null) {
            throw new BusinessException(404, "单位不存在");
        }

        // 检查城市是否存在
        City city = cityMapper.selectById(request.getCityId());
        if (city == null) {
            throw new BusinessException(404, "所属城市不存在");
        }

        String newOrgName = request.getOrg().trim();

        // 检查新单位名称在该城市下是否与其他单位重复
        Organization existingOrg = organizationMapper.selectByNameAndCityId(newOrgName, request.getCityId());
        if (existingOrg != null && !existingOrg.getId().equals(request.getOrgId())) {
            throw new BusinessException(400, "该城市下已存在同名单位");
        }

        // 更新单位信息
        organization.setId(request.getOrgId());
        organization.setOrgName(newOrgName);
        organization.setCityId(request.getCityId());

        int rows = organizationMapper.update(organization);
        if (rows == 0) {
            throw new BusinessException(500, "更新单位失败");
        }

        log.info("更新单位成功：ID={}, 新名称={}, 所属城市={}", request.getOrgId(), newOrgName, city.getCityName());
    }

    /**
     * 删除单位
     *
     * @param id 单位ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrganization(Long id) {
        // 检查单位是否存在
        Organization organization = organizationMapper.selectById(id);
        if (organization == null) {
            throw new BusinessException(404, "单位不存在");
        }

        // 检查单位下是否有学员
        int userCount = organizationMapper.countUsersByOrgId(id);
        if (userCount > 0) {
            throw new BusinessException(400, "该单位下有学员信息，无法删除");
        }

        // 删除单位
        int rows = organizationMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(500, "删除单位失败");
        }

        log.info("删除单位成功：ID={}, 名称={}", id, organization.getOrgName());
    }

    /**
     * 批量删除单位
     *
     * @param ids 单位ID列表
     * @return 删除的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrganizationsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的单位");
        }

        // 检查每个单位下是否有学员
        for (Long id : ids) {
            Organization organization = organizationMapper.selectById(id);
            if (organization == null) {
                throw new BusinessException(404, "单位ID " + id + " 不存在");
            }

            int userCount = organizationMapper.countUsersByOrgId(id);
            if (userCount > 0) {
                throw new BusinessException(400, "单位「" + organization.getOrgName() + "」下有学员信息，无法删除");
            }
        }

        // 批量删除
        int rows = organizationMapper.deleteBatch(ids);
        if (rows == 0) {
            throw new BusinessException(500, "批量删除单位失败");
        }

        log.info("批量删除单位成功：删除数量={}", rows);
        return rows;
    }
}

