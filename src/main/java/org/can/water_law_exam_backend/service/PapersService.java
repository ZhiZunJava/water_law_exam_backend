package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.papers.PapersCreateRequest;
import org.can.water_law_exam_backend.dto.request.template.TemplatePageRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.papers.PapersListVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersAbstractVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersStructVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentItemVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentOptionVO;
import org.can.water_law_exam_backend.entity.*;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PapersService {

    private final PapersMapper papersMapper;
    private final PapersStructMapper papersStructMapper;
    private final PapersContentMapper papersContentMapper;
    private final PapersTemplateMapper papersTemplateMapper;
    private final TemplateDetailMapper templateDetailMapper;
    private final ItemBankMapper itemBankMapper;
    private final ItemTypeMapper itemTypeMapper;
    private final ItemOptionMapper itemOptionMapper;
    private final AdminMapper adminMapper;

    @Transactional(rollbackFor = Exception.class)
    public int create(PapersCreateRequest req) {
        PapersTemplate papersTemplate = papersTemplateMapper.selectByTemplateName(req.getTemplateName());
        if (papersTemplate == null) {
            throw new BusinessException(1, "组卷模板不存在");
        }
        validate(req, papersTemplate);
        // 每个题型的每题分值与数量
        Map<Integer, BigDecimal> scorePerItem = new HashMap<>();
        // Map<Integer, Integer> countPerType = new HashMap<>();
        int sumScore = 0;
        for (PapersCreateRequest.DetailDTO d : req.getDetails()) {
            BigDecimal spi = new BigDecimal(d.getTotalScore()).divide(new BigDecimal(d.getTotality()), 2, RoundingMode.HALF_UP);
            scorePerItem.put(d.getTypeId(), spi);
            //  countPerType.put(d.getTypeId(), d.getTotality());
            sumScore += d.getTotalScore();
        }

        int created = 0;
        for (int no = 1; no <= req.getPapersCount(); no++) {
            Papers paper = new Papers();
            paper.setPapersTitle(req.getTemplateName());
            paper.setPapersNo(no);
            paper.setTotalScore(sumScore);
            paper.setTemplateId(papersTemplate.getId());
            paper.setCreatorId(currentAdminId());
            papersMapper.insert(paper);

            // 生成结构
            List<PapersStruct> structs = new ArrayList<>();
            for (PapersCreateRequest.DetailDTO d : req.getDetails()) {
                PapersStruct ps = new PapersStruct();
                ps.setPapersId(paper.getId());
                ps.setTypeId(d.getTypeId());
                ItemType it = itemTypeMapper.selectById(d.getTypeId());
                if (it != null) {
                    ps.setTypeName(it.getTypeName());
                    ps.setTypeRemarks(it.getTypeRemarks());
                }
                ps.setScore(d.getTotalScore());
                structs.add(ps);
            }
            if (!structs.isEmpty()) {
                papersStructMapper.insertBatch(structs);
            }

            // 生成内容
            List<PapersContent> contents = new ArrayList<>();
            int sort = 1;
            for (PapersCreateRequest.DetailDTO d : req.getDetails()) {
                Integer typeId = d.getTypeId();
                Integer need = d.getTotality();
                int total = itemBankMapper.countByType(typeId);
                if (total < need) {
                    throw new BusinessException(1, "题库不足，题型" + typeId + "需要" + need + "道题，现有" + total);
                }
                // 随机偏移采样：多段分页拼接，避免大表 RAND()
                Set<Long> picked = new LinkedHashSet<>();
                int pageSize = Math.min(need, 50);
                while (picked.size() < need) {
                    int maxStart = Math.max(total - pageSize, 0);
                    int offset = maxStart == 0 ? 0 : ThreadLocalRandom.current().nextInt(0, maxStart + 1);
                    List<Long> batch = itemBankMapper.selectIdsByTypeWithOffset(typeId, pageSize, offset);
                    for (Long idd : batch) {
                        picked.add(idd);
                        if (picked.size() >= need) break;
                    }
                }
                BigDecimal spi = scorePerItem.get(typeId);
                for (Long itemId : picked) {
                    PapersContent pc = new PapersContent();
                    pc.setPapersId(paper.getId());
                    pc.setPapersNo(no);
                    pc.setItemId(itemId);
                    pc.setTypeId(typeId);
                    pc.setScore(spi);
                    pc.setSortOrder(sort++);
                    contents.add(pc);
                }
            }
            if (!contents.isEmpty()) {
                papersContentMapper.insertBatch(contents);
            }
            created++;
        }
        return created;
    }

    public PageResult<PapersListVO> pages(TemplatePageRequest request) {
        String key = request.getParam() != null ? request.getParam().getKey() : null;
        PageHelper.startPage(request.getPage(), request.getSize());
        List<Papers> list = papersMapper.selectByPage(key);
        PageInfo<Papers> pi = new PageInfo<>(list);
        List<PapersListVO> vos = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Papers p : list) {
            PapersListVO vo = new PapersListVO();
            Admin creator = adminMapper.selectById(p.getCreatorId());
            vo.setId(p.getId());
            vo.setNo(p.getPapersNo());
            vo.setTitle(p.getPapersTitle());
            vo.setCreator(creator != null ? creator.getName() : "未知作者");
            vo.setCreateTime(p.getCreateTime() == null ? null : p.getCreateTime().format(fmt));
            vos.add(vo);
        }
        PageInfo<PapersListVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(1, "请选择要删除的试卷");
        }
        papersStructMapper.deleteByPapersIds(ids);
        papersContentMapper.deleteByPapersIds(ids);
        return papersMapper.deleteBatch(ids);
    }

    public PapersAbstractVO abstractInfo(Long id) {
        Papers p = papersMapper.selectById(id);
        if (p == null) throw new BusinessException(1, "试卷不存在");
        PapersAbstractVO vo = new PapersAbstractVO();
        vo.setId(p.getId());
        vo.setTitle(p.getPapersTitle());
        PapersTemplate tpl = papersTemplateMapper.selectById(p.getTemplateId());
        vo.setPapersCount(tpl.getPapersCount());
        vo.setPapersNo(p.getPapersNo());
        vo.setScore(p.getTotalScore());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        vo.setCreateTime(p.getCreateTime() == null ? null : p.getCreateTime().format(fmt));
        return vo;
    }

    public PapersContentVO content(Long id, Integer no) {
        Papers p = papersMapper.selectById(id);
        if (p == null) throw new BusinessException(1, "试卷不存在");
        if (!Objects.equals(p.getPapersNo(), no)) {
            throw new BusinessException(1, "试卷对应序号不正确");
        }
        PapersContentVO resp = new PapersContentVO();
        resp.setId(p.getId());
        resp.setNo(no);
        resp.setTitle(p.getPapersTitle());
        resp.setTotalScore(p.getTotalScore());

        // structs
        List<PapersStruct> structs = papersStructMapper.selectByPapersId(id);
        List<PapersStructVO> structVOs = new ArrayList<>();
        for (PapersStruct s : structs) {
            PapersStructVO sv = new PapersStructVO();
            sv.setPapersId(s.getPapersId());
            sv.setTypeName(s.getTypeName());
            sv.setTypeRemarks(s.getTypeRemarks());
            sv.setScore(s.getScore());
            structVOs.add(sv);
        }
        resp.setStructs(structVOs);

        // content
        List<PapersContent> pcs = papersContentMapper.selectByPapersIdAndNo(id, no);
        // group by type
        Map<Integer, List<PapersContentItemVO>> grouped = new LinkedHashMap<>();
        for (PapersContent pc : pcs) {
            grouped.computeIfAbsent(pc.getTypeId(), k -> new ArrayList<>());
            ItemBank ibFull = itemBankMapper.selectById(pc.getItemId());
            PapersContentItemVO item = new PapersContentItemVO();
            item.setId(pc.getItemId());
            item.setScore(pc.getScore());
            if (ibFull != null) {
                item.setTypeName(ibFull.getTypeName());
                item.setContent(ibFull.getContent());
                // 选项
                List<ItemOption> opts = itemOptionMapper.selectByItemId(pc.getItemId());
                List<PapersContentOptionVO> optVOs = new ArrayList<>();
                if (pc.getTypeId() != null && pc.getTypeId() == 3) {
                    // 判断题：返回两个选项，正确的checked=true，错误的checked=null
                    for (ItemOption op : opts) {
                        PapersContentOptionVO ov = new PapersContentOptionVO();
                        ov.setTitle(op.getOptionTitle());
                        ov.setChecked(op.getIsCorrect() != null && op.getIsCorrect() ? Boolean.TRUE : null);
                        optVOs.add(ov);
                    }
                } else {
                    for (ItemOption op : opts) {
                        PapersContentOptionVO ov = new PapersContentOptionVO();
                        ov.setTitle(op.getOptionTitle());
                        ov.setChecked(op.getIsCorrect() != null && op.getIsCorrect() ? Boolean.TRUE : null);
                        optVOs.add(ov);
                    }
                }
                item.setOptions(optVOs);
            }
            grouped.get(pc.getTypeId()).add(item);
        }
        // 转换为以题型中文名为key
        Map<String, List<PapersContentItemVO>> contentByType = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<PapersContentItemVO>> e : grouped.entrySet()) {
            ItemType t = itemTypeMapper.selectById(e.getKey());
            contentByType.put(t != null ? t.getTypeName() : String.valueOf(e.getKey()), e.getValue());
        }
        resp.setContent(contentByType);
        return resp;
    }

    private void validate(PapersCreateRequest req, PapersTemplate template) {
        List<TemplateDetail> templateDetails = templateDetailMapper.selectByTemplateId(template.getId());
        if (templateDetails.isEmpty()) {
            throw new BusinessException(1, "模板未配置题型详情");
        }
        if (req.getDetails() == null || req.getDetails().isEmpty()) {
            throw new BusinessException(1, "明细不能为空");
        }
        if (!Objects.equals(req.getPapersCount(), template.getPapersCount())) {
            throw new BusinessException(1, "试卷数量不一致，应为：" + template.getPapersCount());
        }
        Map<Integer, TemplateDetail> detailMap = templateDetails.stream()
                .collect(Collectors.toMap(TemplateDetail::getTypeId, d -> d, (k1, k2) -> k1));
        int sum = 0;
        for (PapersCreateRequest.DetailDTO d : req.getDetails()) {
            Integer questionTypeId = d.getTypeId();
            ItemType t = itemTypeMapper.selectById(questionTypeId);
            TemplateDetail templateDetail = detailMap.get(questionTypeId);
            if (templateDetail == null) {
                throw new BusinessException(1, "题型[" + t.getTypeName() + "]不在模板允许范围内");
            }

            Integer totality = d.getTotality();
            if (totality == null || totality <= 0) {
                throw new BusinessException(1, "题量必须大于0");
            }
            if (Double.compare(totality, templateDetail.getItemCount()) != 0) {
                throw new BusinessException(1, "题型[" + t.getTypeName() + "]题量不一致，应为：" + templateDetail.getItemCount());
            }

            if (d.getTotalScore() == null || d.getTotalScore() <= 0) {
                throw new BusinessException(1, "总分必须大于0");
            }
            int totalScore = templateDetail.getScorePerItem()
                    .multiply(BigDecimal.valueOf(templateDetail.getItemCount()))
                    .intValue();
            if (Double.compare(d.getTotalScore(), totalScore) != 0) {
                throw new BusinessException(1, "题型[" + t.getTypeName() + "]总分不一致，应为：" + totalScore);

            }
            sum += d.getTotalScore();
        }
        if (!Objects.equals(sum, req.getMaxScore())) {
            throw new BusinessException(1, "各题型总分之和必须等于总分");
        }
    }

    private Long currentAdminId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return 0L;
            String name = auth.getName();
            if (name != null) {
                Admin admin = adminMapper.selectByUserNo(name);
                return admin.getId();
            }
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
