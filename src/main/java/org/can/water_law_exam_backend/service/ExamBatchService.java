package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.batch.BatchAddRequest;
import org.can.water_law_exam_backend.dto.request.batch.BatchPageRequest;
import org.can.water_law_exam_backend.dto.request.batch.BatchUpdateRequest;
import org.can.water_law_exam_backend.dto.response.batch.BatchVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.entity.ExamBatch;
import org.can.water_law_exam_backend.entity.PapersGroup;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.ExamBatchMapper;
import org.can.water_law_exam_backend.mapper.PapersGroupMapper;
import org.can.water_law_exam_backend.mapper.PapersMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamBatchService {

    private final ExamBatchMapper examBatchMapper;
    private final PapersGroupMapper papersGroupMapper;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(rollbackFor = Exception.class)
    public Long add(BatchAddRequest req) {
        validateTime(req.getStartTime(), req.getEndTime());
        // 批次现在选择的是试卷组ID
        PapersGroup group = papersGroupMapper.selectById(req.getPapersId());
        if (group == null) {
            throw new BusinessException(1, "试卷组不存在");
        }
        ExamBatch entity = new ExamBatch();
        entity.setBatchName(req.getBatchName());
        entity.setStartTime(LocalDateTime.parse(req.getStartTime(), DATETIME_FMT));
        entity.setEndTime(LocalDateTime.parse(req.getEndTime(), DATETIME_FMT));
        entity.setPrepareMinutes(req.getPrepareMinutes());
        entity.setAdvanceMinutes(req.getAdvanceMinutes());
        entity.setLateMinutes(req.getLateMinutes());
        entity.setOptionsRandom(req.getOptionsRandom());
        entity.setItemRandom(req.getItemRandom());
        entity.setPapersId(req.getPapersId());
        entity.setSelfJoin(req.getSelfJoin());
        entity.setReviewRequired(req.getReviewRequired());
        entity.setReleased(false);
        entity.setPapersDistributed(false);
        examBatchMapper.insert(entity);
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(BatchUpdateRequest req) {
        ExamBatch db = examBatchMapper.selectById(req.getId());
        if (db == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        validateTime(req.getStartTime(), req.getEndTime());
        PapersGroup group = papersGroupMapper.selectById(req.getPapersId());
        if (group == null) {
            throw new BusinessException(1, "试卷组不存在");
        }
        db.setBatchName(req.getBatchName());
        db.setStartTime(LocalDateTime.parse(req.getStartTime(), DATETIME_FMT));
        db.setEndTime(LocalDateTime.parse(req.getEndTime(), DATETIME_FMT));
        db.setPrepareMinutes(req.getPrepareMinutes());
        db.setAdvanceMinutes(req.getAdvanceMinutes());
        db.setLateMinutes(req.getLateMinutes());
        db.setOptionsRandom(req.getOptionsRandom());
        db.setItemRandom(req.getItemRandom());
        db.setPapersId(req.getPapersId());
        db.setSelfJoin(req.getSelfJoin());
        db.setReviewRequired(req.getReviewRequired());
        examBatchMapper.update(db);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(1, "请选择要删除的考试批次");
        }
        return examBatchMapper.deleteBatch(ids);
    }

    /**
     * 分发/收回试卷：返回 true 表示本次操作后为“已分发”，false 表示本次操作后为“已收回”
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleDistribute(Long id) {
        ExamBatch batch = examBatchMapper.selectById(id);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        if (!Boolean.TRUE.equals(batch.getReleased())) {
            throw new BusinessException(1, "考试未发布，不能分发试卷");
        }
        boolean newDistributed = !Boolean.TRUE.equals(batch.getPapersDistributed());
        batch.setPapersDistributed(newDistributed);
        // 如果收回，则保持 released 状态不变，但可以在业务上提示前端需同步处理
        examBatchMapper.update(batch);
        return newDistributed;
    }

    /**
     * 发布/未发布考试：返回 true 表示本次操作后为“已发布”，false 表示为“未发布”
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleRelease(Long id) {
        ExamBatch batch = examBatchMapper.selectById(id);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        boolean newReleased = !Boolean.TRUE.equals(batch.getReleased());
        batch.setReleased(newReleased);
        // 取消发布时，需要撤回已分发的试卷
        if (!newReleased) {
            batch.setPapersDistributed(false);
        }
        examBatchMapper.update(batch);
        return newReleased;
    }

    /**
     * 8.1.5 批次列表 - 考试未开始（已启用=已发布且试卷已分发）
     */
    public List<BatchVO> listEnabledNotStarted() {
        List<ExamBatch> list = examBatchMapper.selectEnabledNotStarted(LocalDateTime.now());
        List<BatchVO> vos = new ArrayList<>();
        for (ExamBatch b : list) {
            vos.add(toVO(b, true));
        }
        return vos;
    }

    /**
     * 8.1.6 批次列表 - 分页（全部）
     */
    public PageResult<BatchVO> pages(BatchPageRequest req) {
        String key = req.getParam() != null ? req.getParam().getKey() : null;
        Boolean lock = req.getParam() != null ? req.getParam().getLock() : null;
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ExamBatch> list = examBatchMapper.selectByPage(key, lock);
        PageInfo<ExamBatch> pi = new PageInfo<>(list);
        List<BatchVO> vos = new ArrayList<>();
        for (ExamBatch b : list) {
            vos.add(toVO(b, null));
        }
        PageInfo<BatchVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    /**
     * 8.1.7 获取单个批次信息
     */
    public BatchVO getOne(Long id) {
        ExamBatch b = examBatchMapper.selectById(id);
        if (b == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        return toVO(b, null);
    }

    private BatchVO toVO(ExamBatch b, Boolean forceStatus) {
        BatchVO vo = new BatchVO();
        vo.setId(b.getId());
        vo.setBatchName(b.getBatchName());
        vo.setPapersId(b.getPapersId());
        vo.setStartTime(b.getStartTime() == null ? null : b.getStartTime().format(DATETIME_FMT));
        vo.setEndTime(b.getEndTime() == null ? null : b.getEndTime().format(DATETIME_FMT));
        vo.setLateMinutes(b.getLateMinutes());
        vo.setPrepareMinutes(b.getPrepareMinutes());
        vo.setAdvanceMinutes(b.getAdvanceMinutes());
        vo.setOptionsRandom(b.getOptionsRandom());
        vo.setItemRandom(b.getItemRandom());
        vo.setReleased(b.getReleased());
        vo.setSelfJoin(b.getSelfJoin());
        vo.setReviewRequired(b.getReviewRequired());
        // status: true 表示“启用”（已发布且试卷已分发）
        if (forceStatus != null) {
            vo.setStatus(forceStatus);
        } else {
            vo.setStatus(Boolean.TRUE.equals(b.getReleased()) && Boolean.TRUE.equals(b.getPapersDistributed()));
        }
        return vo;
    }

    private void validateTime(String start, String end) {
        LocalDateTime s = LocalDateTime.parse(start, DATETIME_FMT);
        LocalDateTime e = LocalDateTime.parse(end, DATETIME_FMT);
        if (!e.isAfter(s)) {
            throw new BusinessException(1, "考试结束时间必须晚于开始时间");
        }
    }
}



