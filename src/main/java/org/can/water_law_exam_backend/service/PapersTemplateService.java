package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.template.TemplateAddRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplatePageRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplateUpdateRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.template.TemplateDetailVO;
import org.can.water_law_exam_backend.dto.response.template.TemplateVO;
import org.can.water_law_exam_backend.entity.PapersTemplate;
import org.can.water_law_exam_backend.entity.TemplateDetail;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.PapersTemplateMapper;
import org.can.water_law_exam_backend.mapper.TemplateDetailMapper;
import org.can.water_law_exam_backend.mapper.ItemTypeMapper;
import org.can.water_law_exam_backend.entity.ItemType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PapersTemplateService {

    private final PapersTemplateMapper papersTemplateMapper;
    private final TemplateDetailMapper templateDetailMapper;
    private final ItemTypeMapper itemTypeMapper;
    private final PapersService papersService;

    @Transactional(rollbackFor = Exception.class)
    public int add(TemplateAddRequest request) {
        PapersTemplate papersTemplate = papersTemplateMapper.selectByTemplateName(request.getTemplateName());
        if  (papersTemplate != null) {
            throw new BusinessException(1, "组卷模板名称不允许重复");
        }
        validateTemplate(request.getMaxScore(), request.getDetails());

        PapersTemplate entity = new PapersTemplate();
        entity.setTemplateName(request.getTemplateName());
        entity.setPapersCount(request.getPapersCount());
        entity.setMaxScore(request.getMaxScore());
        entity.setKeyProportion(request.getKeyProportion());
        int rows = papersTemplateMapper.insert(entity);
        if (rows == 0 || entity.getId() == null) {
            throw new BusinessException(1, "添加模板失败");
        }
        List<TemplateDetail> details = buildDetails(entity.getId(), request.getDetails());
        if (!details.isEmpty()) {
            templateDetailMapper.insertBatch(details);
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(TemplateUpdateRequest request) {
        validateTemplate(request.getMaxScore(), request.getDetails());
        PapersTemplate entity = new PapersTemplate();
        entity.setId(request.getId());
        entity.setTemplateName(request.getTemplateName());
        entity.setPapersCount(request.getPapersCount());
        entity.setMaxScore(request.getMaxScore());
        entity.setKeyProportion(request.getKeyProportion());
        int rows = papersTemplateMapper.update(entity);
        if (rows == 0) {
            throw new BusinessException(1, "修改模板失败");
        }
        templateDetailMapper.deleteByTemplateId(request.getId());
        List<TemplateDetail> details = buildDetails(request.getId(), request.getDetails());
        if (!details.isEmpty()) {
            templateDetailMapper.insertBatch(details);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(1, "请选择要删除的模板");
        }
        List<Long> ids1 = new ArrayList<>();
        // 先删明细
        for (Integer id : ids) {
            ids1.add(Long.valueOf(id));
            templateDetailMapper.deleteByTemplateId(id);
        }
        int rows = papersTemplateMapper.deleteBatch(ids);
        if (rows == 0) {
            throw new BusinessException(1, "删除失败");
        }
        papersService.deleteBatch(ids1);
        return rows;
    }

    public TemplateVO getById(Integer id) {
        PapersTemplate pt = papersTemplateMapper.selectById(id);
        if (pt == null) {
            throw new BusinessException(1, "模板不存在");
        }
        TemplateVO vo = toVO(pt, true);
        return vo;
    }

    public PageResult<TemplateVO> pages(TemplatePageRequest request) {
        String key = request.getParam() != null ? request.getParam().getKey() : null;
        boolean needDetail = request.getParam() != null && Boolean.TRUE.equals(request.getParam().getDetail());

        PageHelper.startPage(request.getPage(), request.getSize());
        List<PapersTemplate> list = papersTemplateMapper.selectByPage(key);
        PageInfo<PapersTemplate> pi = new PageInfo<>(list);

        List<TemplateVO> vos = new ArrayList<>();
        for (PapersTemplate pt : list) {
            vos.add(toVO(pt, needDetail));
        }
        PageInfo<TemplateVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    private void validateTemplate(Integer maxScore, List<?> details) {
        if (details == null || details.isEmpty()) {
            throw new BusinessException(1, "模板明细不能为空");
        }
        int sumScore = 0;
        for (Object obj : details) {
            Integer totality;
            Integer totalScore;
            if (obj instanceof TemplateAddRequest.DetailDTO d) {
                totality = d.getTotality();
                totalScore = d.getTotalScore();
            } else if (obj instanceof TemplateUpdateRequest.DetailDTO d) {
                totality = d.getTotality();
                totalScore = d.getTotalScore();
            } else {
                continue;
            }
            if (totality == null || totality <= 0 || totalScore == null || totalScore <= 0) {
                throw new BusinessException(1, "明细题量与总分必须大于0");
            }
            sumScore += totalScore;
        }
        if (!Objects.equals(sumScore, maxScore)) {
            throw new BusinessException(1, "各题型总分之和必须等于模板总分");
        }
    }

    private List<TemplateDetail> buildDetails(Integer templateId, List<?> detailDTOs) {
        List<TemplateDetail> list = new ArrayList<>();
        for (Object obj : detailDTOs) {
            Integer typeId;
            Integer totality;
            Integer totalScore;
            if (obj instanceof TemplateAddRequest.DetailDTO d) {
                typeId = d.getTypeId();
                totality = d.getTotality();
                totalScore = d.getTotalScore();
            } else {
                TemplateUpdateRequest.DetailDTO d = (TemplateUpdateRequest.DetailDTO) obj;
                typeId = d.getTypeId();
                totality = d.getTotality();
                totalScore = d.getTotalScore();
            }
            TemplateDetail td = new TemplateDetail();
            td.setTemplateId(templateId);
            td.setTypeId(typeId);
            td.setItemCount(totality);
            BigDecimal spi = new BigDecimal(totalScore).divide(new BigDecimal(totality), 2, RoundingMode.HALF_UP);
            td.setScorePerItem(spi);
            list.add(td);
        }
        return list;
    }

    private TemplateVO toVO(PapersTemplate pt, boolean withDetail) {
        TemplateVO vo = new TemplateVO();
        BeanUtils.copyProperties(pt, vo);
        if (withDetail) {
            List<TemplateDetail> ds = templateDetailMapper.selectByTemplateId(pt.getId());
            List<TemplateDetailVO> dvos = new ArrayList<>();
            for (TemplateDetail d : ds) {
                TemplateDetailVO dv = new TemplateDetailVO();
                dv.setTypeId(d.getTypeId());
                dv.setTotality(d.getItemCount());
                dv.setScorePerItem(d.getScorePerItem());
                // totalScore = totality * scorePerItem
                if (d.getScorePerItem() != null && d.getItemCount() != null) {
                    dv.setTotalScore(d.getScorePerItem().multiply(new BigDecimal(d.getItemCount())).setScale(0, RoundingMode.HALF_UP).intValue());
                }
                // 填充题型说明
                ItemType it = itemTypeMapper.selectById(d.getTypeId());
                if (it != null) {
                    dv.setRemarks(it.getTypeRemarks());
                }
                dvos.add(dv);
            }
            vo.setDetails(dvos);
        }
        return vo;
    }
}
