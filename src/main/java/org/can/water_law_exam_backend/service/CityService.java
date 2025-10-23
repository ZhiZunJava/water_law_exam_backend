package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.city.CityAddRequest;
import org.can.water_law_exam_backend.dto.request.city.CityPageRequest;
import org.can.water_law_exam_backend.dto.request.city.CityUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.entity.City;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.CityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 城市服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityMapper cityMapper;

    /**
     * 获取所有城市列表
     *
     * @return 城市列表
     */
    public List<City> getAllCities() {
        return cityMapper.selectAll();
    }

    /**
     * 分页查询城市列表
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    public PageResult<City> getCitiesByPage(CityPageRequest request) {
        // 计算偏移量
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 获取查询关键字
        String key = null;
        if (request.getParam() != null && request.getParam().getKey() != null) {
            key = request.getParam().getKey().trim();
            if (key.isEmpty()) {
                key = null;
            }
        }

        // 查询数据列表
        List<City> list = cityMapper.selectByPage(offset, request.getSize(), key);

        // 统计总数（如果需要）
        long total;
        if (request.getTotal() != null && request.getTotal() >= 0) {
            // 使用传入的总数，避免重复统计
            total = request.getTotal().longValue();
        } else {
            // 重新统计总数
            total = cityMapper.countAll(key);
        }

        return new PageResult<>(total, list);
    }

    /**
     * 根据ID获取城市信息
     *
     * @param id 城市ID
     * @return 城市信息
     */
    public City getCityById(Integer id) {
        City city = cityMapper.selectById(id);
        if (city == null) {
            throw new BusinessException(404, "城市不存在");
        }
        return city;
    }

    /**
     * 添加城市
     *
     * @param request 添加请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCity(CityAddRequest request) {
        String cityName = request.getCity().trim();

        // 检查城市名称是否已存在
        City existingCity = cityMapper.selectByName(cityName);
        if (existingCity != null) {
            throw new BusinessException(400, "城市名称已存在");
        }

        // 创建城市对象
        City city = new City();
        city.setCityName(cityName);

        // 插入数据库
        int rows = cityMapper.insert(city);
        if (rows == 0) {
            throw new BusinessException(500, "添加城市失败");
        }

        log.info("添加城市成功：{}", cityName);
    }

    /**
     * 更新城市信息
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCity(CityUpdateRequest request) {
        // 检查城市是否存在
        City city = cityMapper.selectById(request.getCityId());
        if (city == null) {
            throw new BusinessException(404, "城市不存在");
        }

        String newCityName = request.getCity().trim();

        // 检查新城市名称是否与其他城市重复
        City existingCity = cityMapper.selectByName(newCityName);
        if (existingCity != null && !existingCity.getId().equals(request.getCityId())) {
            throw new BusinessException(400, "城市名称已存在");
        }

        // 更新城市信息
        city.setId(request.getCityId());
        city.setCityName(newCityName);

        int rows = cityMapper.update(city);
        if (rows == 0) {
            throw new BusinessException(500, "更新城市失败");
        }

        log.info("更新城市成功：ID={}, 新名称={}", request.getCityId(), newCityName);
    }

    /**
     * 删除城市
     *
     * @param id 城市ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCity(Integer id) {
        // 检查城市是否存在
        City city = cityMapper.selectById(id);
        if (city == null) {
            throw new BusinessException(404, "城市不存在");
        }

        // 检查城市下是否有单位
        int orgCount = cityMapper.countOrganizationsByCityId(id);
        if (orgCount > 0) {
            throw new BusinessException(400, "该城市下有单位信息，无法删除");
        }

        // 删除城市
        int rows = cityMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(500, "删除城市失败");
        }

        log.info("删除城市成功：ID={}, 名称={}", id, city.getCityName());
    }

    /**
     * 批量删除城市
     *
     * @param ids 城市ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCitiesBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的城市");
        }

        // 检查每个城市下是否有单位
        for (Integer id : ids) {
            City city = cityMapper.selectById(id);
            if (city == null) {
                throw new BusinessException(404, "城市ID " + id + " 不存在");
            }

            int orgCount = cityMapper.countOrganizationsByCityId(id);
            if (orgCount > 0) {
                throw new BusinessException(400, "城市「" + city.getCityName() + "」下有单位信息，无法删除");
            }
        }

        // 批量删除
        int rows = cityMapper.deleteBatch(ids);
        if (rows == 0) {
            throw new BusinessException(500, "批量删除城市失败");
        }

        log.info("批量删除城市成功：删除数量={}", rows);
    }
}

