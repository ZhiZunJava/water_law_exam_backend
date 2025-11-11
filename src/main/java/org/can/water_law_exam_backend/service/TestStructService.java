package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import org.can.water_law_exam_backend.dto.request.teststruct.TestStructSetRequest;
import org.can.water_law_exam_backend.dto.response.teststruct.TestStructVO;
import org.can.water_law_exam_backend.entity.TestStruct;
import org.can.water_law_exam_backend.mapper.TestStructMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestStructService {

    private final TestStructMapper testStructMapper;

    @Transactional(rollbackFor = Exception.class)
    public void setStruct(List<TestStructSetRequest> reqList) {
        testStructMapper.deleteAll();
        List<TestStruct> toSave = new ArrayList<>();
        for (TestStructSetRequest r : reqList) {
            TestStruct ts = new TestStruct();
            ts.setTypeId(r.getTypeId());
            ts.setTypeRemarks(r.getRemarks());
            ts.setScore(r.getScore());
            ts.setTotality(r.getTotality());
            toSave.add(ts);
        }
        if (!toSave.isEmpty()) {
            testStructMapper.insertBatch(toSave);
        }
    }

    public List<TestStructVO> getStruct() {
        List<TestStruct> list = testStructMapper.selectAllWithType();
        return list.stream().map(ts -> {
            TestStructVO vo = new TestStructVO();
            vo.setTypeId(ts.getTypeId());
            vo.setTypeName(ts.getTypeName());
            vo.setRemarks(ts.getTypeRemarks());
            vo.setScore(ts.getScore());
            vo.setTotality(ts.getTotality());
            return vo;
        }).collect(Collectors.toList());
    }
}


