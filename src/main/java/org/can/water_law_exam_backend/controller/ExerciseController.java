package org.can.water_law_exam_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.response.exam.ExamPapersVO;
import org.can.water_law_exam_backend.dto.response.teststruct.TestPapersVO;
import org.can.water_law_exam_backend.dto.response.teststruct.TestQuestionVO;
import org.can.water_law_exam_backend.dto.response.teststruct.TestStructVO;
import org.can.water_law_exam_backend.service.ItemBankService;
import org.can.water_law_exam_backend.service.TestStructService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final TestStructService testStructService;
    private final ItemBankService itemBankService;

    /**
     * 4.1 该接口用于随机获取一组用于测试的试题
     * GET /exercise/test
     */
    @GetMapping("/test")
    public Result<TestPapersVO> test() {
        int totalScore = 0;
        // 1. 先获取题型结构列表（原有逻辑，用于统计数量）
        List<TestStructVO> structList = testStructService.getStruct();

        // 2. 统计每种题型的数量（key：typeId，value：题目数量，对应你要的3种题型）
        Map<Integer, Integer> typeCountMap = new HashMap<>();
        for (TestStructVO vo : structList) {
            Integer typeId = vo.getTypeId();
            typeCountMap.put(typeId, vo.getTotality());

            if (vo.getScore() != null) {
                totalScore += vo.getScore();
            }
        }

        // 3. 定义3种题型（可根据你的实际typeId修改，比如1=单选、2=多选、3=判断）
        Integer singleChoiceTypeId = 1; // 单选题ID
        Integer multipleChoiceTypeId = 2; // 多选题ID
        Integer judgmentTypeId = 3; // 判断题ID

        // 4. 获取3种题型对应的题目数量（避免空指针，默认0）
        int singleChoiceCount = typeCountMap.getOrDefault(singleChoiceTypeId, 0);
        int multipleChoiceCount = typeCountMap.getOrDefault(multipleChoiceTypeId, 0);
        int judgmentCount = typeCountMap.getOrDefault(judgmentTypeId, 0);

        // 5. 根据题型ID和数量，获取对应题目内容（核心步骤：查询题目+选项）
        // 存储最终要返回的所有题目（按题型分组，方便前端展示）
        Map<String, List<TestQuestionVO>> questionMap = new LinkedHashMap<>();

        // 5.1 获取单选题内容
        if (singleChoiceCount > 0) {
            List<TestQuestionVO> singleChoiceList = itemBankService.getQuestionListByTypeId(singleChoiceTypeId, singleChoiceCount);
            questionMap.put("单选题", singleChoiceList);
        }

        // 5.2 获取多选题内容
        if (multipleChoiceCount > 0) {
            List<TestQuestionVO> multipleChoiceList = itemBankService.getQuestionListByTypeId(multipleChoiceTypeId, multipleChoiceCount);
            questionMap.put("多选题", multipleChoiceList);
        }

        // 5.3 获取判断题内容
        if (judgmentCount > 0) {
            List<TestQuestionVO> judgmentList = itemBankService.getQuestionListByTypeId(judgmentTypeId, judgmentCount);
            questionMap.put("判断题", judgmentList);
        }

        // 6. 封装返回数据（需自定义VO，承载题型数量+题目内容）
        TestPapersVO examPapersVO = new TestPapersVO();
        examPapersVO.setTotalScore(totalScore);
        // 设置3种题型数量
        examPapersVO.setSingleChoiceCount(singleChoiceCount);
        examPapersVO.setMultipleChoiceCount(multipleChoiceCount);
        examPapersVO.setJudgmentCount(judgmentCount);
        // 设置题目内容
        examPapersVO.setQuestionMap(questionMap);
        // 设置题型结构（可选，返回给前端题型名称、分值等信息）
        examPapersVO.setTestStructList(structList);

        // 7. 返回给前端
        return Result.success(examPapersVO);
    }
}
