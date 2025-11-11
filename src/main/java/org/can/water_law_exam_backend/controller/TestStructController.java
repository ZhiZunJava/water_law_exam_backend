package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.teststruct.TestStructSetRequest;
import org.can.water_law_exam_backend.dto.response.teststruct.TestStructVO;
import org.can.water_law_exam_backend.service.TestStructService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestStructController {

    private final TestStructService testStructService;

    /**
     * 11.1 设置在线测试的试题构成信息
     * POST /test/struct
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/struct")
    public Result<String> setStruct(@Valid @RequestBody List<TestStructSetRequest> request) {
        testStructService.setStruct(request);
        return Result.success("成功设置试题");
    }

    /**
     * 11.2 获取在线测试试题构成信息
     * GET /test/struct
     */
    @GetMapping("/struct")
    public Result<List<TestStructVO>> getStruct() {
        List<TestStructVO> list = testStructService.getStruct();
        return Result.success(list);
    }
}


