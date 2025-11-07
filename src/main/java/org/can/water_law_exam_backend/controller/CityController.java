package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.common.constant.ResultCodeEnum;
import org.can.water_law_exam_backend.dto.request.city.CityAddRequest;
import org.can.water_law_exam_backend.dto.request.city.CityPageRequest;
import org.can.water_law_exam_backend.dto.request.city.CityUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.entity.City;
import org.can.water_law_exam_backend.service.CityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市控制器
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    /**
     * 获取所有城市列表
     *
     * @return 城市列表
     */
    @GetMapping("/list")
    public Result<List<City>> getAllCities() {
        log.info("获取所有城市列表");
        List<City> cities = cityService.getAllCities();
        return Result.success(cities);
    }

    /**
     * 分页查询城市列表
     *
     * @return 分页结果
     */
    @GetMapping("/pages")
    public Result<PageResult<City>> getCitiesByPage( @Valid @RequestBody CityPageRequest request) {
        log.info("分页查询城市列表：page={}, size={}, total={}, key={}", request.getPage(), request.getSize(), request.getTotal(), request.getParam().getKey());
        PageResult<City> result = cityService.getCitiesByPage(request);
        return Result.success(result);
    }

    /**
     * 根据 ID 获取城市信息
     *
     * @param id 城市 ID
     * @return 城市信息
     */
    @GetMapping("/{id}")
    public Result<City> getCityById(@PathVariable Integer id) {
        log.info("获取城市信息：id={}", id);
        City city = cityService.getCityById(id);
        return Result.success(city);
    }

    /**
     * 添加城市
     *
     * @param request 添加请求
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Void> addCity(@Valid @RequestBody CityAddRequest request) {
        log.info("添加城市：{}", request.getCity());
        cityService.addCity(request);
        return Result.success("添加城市成功", null);
    }

    /**
     * 更新城市信息
     *
     * @param request 更新请求
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<Void> updateCity(@Valid @RequestBody CityUpdateRequest request) {
        log.info("更新城市信息：id={}, city={}", request.getCityId(), request.getCity());
        cityService.updateCity(request);
        return Result.success("更新城市成功", null);
    }

    /**
     * 删除城市
     *
     * @param id 城市 ID
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteCity(@PathVariable Integer id) {
        log.info("删除城市：id={}", id);
        cityService.deleteCity(id);
        return Result.success("删除城市成功", null);
    }

    /**
     * 批量删除城市
     *
     * @param ids 城市 ID 列表
     * @return 结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<Void> deleteCitiesBatch(@RequestBody List<Integer> ids) {
        log.info("批量删除城市：ids={}", ids);
        cityService.deleteCitiesBatch(ids);
        return Result.success("批量删除城市成功", null);
    }
}

