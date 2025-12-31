package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.excel.ExamineeExcelData;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineeOptionalPageRequest;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineePageRequest;
import org.can.water_law_exam_backend.dto.request.examinee.ExamineeReviewRequest;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.dto.response.examinee.ExamineePageVO;
import org.can.water_law_exam_backend.entity.AccountUser;
import org.can.water_law_exam_backend.entity.ExamBatch;
import org.can.water_law_exam_backend.entity.Examinee;
import org.can.water_law_exam_backend.entity.Organization;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.AccountUserMapper;
import org.can.water_law_exam_backend.mapper.ExamBatchMapper;
import org.can.water_law_exam_backend.mapper.ExamineeMapper;
import org.can.water_law_exam_backend.mapper.OrganizationMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamineeService {

    private final ExamineeMapper examineeMapper;
    private final ExamBatchMapper examBatchMapper;
    private final AccountUserMapper accountUserMapper;
    private final OrganizationMapper organizationMapper;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 8.2.1 导入考生
     */
    @Transactional(rollbackFor = Exception.class)
    public int importExaminee(MultipartFile file, Long batchId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(1, "上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new BusinessException(1, "只支持Excel 2007版本以后格式文件(.xlsx)");
        }
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }

        try (InputStream is = file.getInputStream()) {
            ExamineeImportListener listener = new ExamineeImportListener(batchId);
            FastExcel.read(is, ExamineeExcelData.class, listener)
                    .sheet("考生导入模版")
                    .doRead();
            int success = listener.getSuccessCount();
            int fail = listener.getFailCount();
            log.info("导入考生完成：成功{}条，失败{}条", success, fail);
            if (success == 0) {
                throw new BusinessException(1, "导入失败：没有成功导入任何考生");
            }
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入考生失败：{}", e.getMessage(), e);
            throw new BusinessException(1, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 考生导入监听器：按行解析Excel，必要时创建学员并绑定到批次
     */
    @lombok.Getter
    private class ExamineeImportListener implements ReadListener<ExamineeExcelData> {

        private final Long batchId;
        private int successCount = 0;
        private int failCount = 0;

        public ExamineeImportListener(Long batchId) {
            this.batchId = batchId;
        }

        @Override
        public void invoke(ExamineeExcelData data, AnalysisContext context) {
            int row = context.readRowHolder().getRowIndex() + 1;
            try {
                if (data == null) {
                    log.warn("行{}：数据为空，跳过", row);
                    failCount++;
                    return;
                }
                String name = trim(data.getName());
                String idNo = trim(data.getIdNo());
                String phone = trim(data.getPhone());
                String orgName = trim(data.getOrgName());
                String cityIdStr = trim(data.getCityId());

                if (name == null || idNo == null || phone == null || orgName == null) {
                    log.warn("行{}：姓名/身份证号/手机号/单位名称有空值，跳过", row);
                    failCount++;
                    return;
                }

                // 查找或创建单位
                Long orgId = null;
                Integer cityId = null;
                if (cityIdStr != null) {
                    try {
                        cityId = Integer.parseInt(cityIdStr);
                    } catch (NumberFormatException e) {
                        log.warn("行{}：城市ID格式错误 '{}', 忽略城市ID，仅按单位名称匹配", row, cityIdStr);
                    }
                }

                Organization org = null;
                if (cityId != null) {
                    org = organizationMapper.selectByNameAndCityId(orgName, cityId);
                }
                if (org == null) {
                    // 如果 cityId 为空或未匹配到，则尝试在所有城市中按名称模糊匹配（这里简化：分页查询首个匹配）
                    List<Organization> list = organizationMapper.selectByPage(null, orgName);
                    if (!list.isEmpty()) {
                        org = list.get(0);
                    }
                }
                if (org == null) {
                    log.warn("行{}：未找到单位'{}'，跳过", row, orgName);
                    failCount++;
                    return;
                }
                orgId = org.getId();

                // 查找或创建学员（按身份证号唯一）
                AccountUser user = accountUserMapper.selectByIdNo(idNo);
                if (user == null) {
                    user = new AccountUser();
                    user.setName(name);
                    user.setOrgId(orgId);
                    user.setIdNo(idNo);
                    user.setPhone(phone);
                    // 默认密码：身份证号后6位
                    String pwdRaw = idNo.length() >= 6 ? idNo.substring(idNo.length() - 6) : idNo;
                    user.setPwd(passwordEncoder.encode(pwdRaw));
                    user.setLocked(false);
                    int rows = accountUserMapper.insert(user);
                    if (rows == 0) {
                        log.warn("行{}：创建学员失败，身份证号={}", row, idNo);
                        failCount++;
                        return;
                    }
                }

                // 绑定到批次（如果已存在则由 ON DUPLICATE KEY 逻辑去重）
                Examinee e = new Examinee();
                e.setBatchId(batchId);
                e.setUserId(user.getId());
                e.setReviewStatus(0);
                List<Examinee> one = new ArrayList<>();
                one.add(e);
                examineeMapper.insertBatch(one);

                successCount++;
            } catch (Exception ex) {
                log.warn("行{}：导入失败，错误：{}", row, ex.getMessage());
                failCount++;
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("考生Excel全部解析完成");
        }

        private String trim(String v) {
            return v == null ? null : v.trim().isEmpty() ? null : v.trim();
        }
    }

    /**
     * 8.2.2 添加考生（绑定到批次）
     */
    @Transactional(rollbackFor = Exception.class)
    public int bindExaminees(Long batchId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(1, "请选择要添加的考生");
        }
        ExamBatch batch = examBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        List<Examinee> list = new ArrayList<>();
        for (Long uid : userIds) {
            AccountUser user = accountUserMapper.selectById(uid);
            if (user == null) {
                throw new BusinessException(1, "学员不存在，ID=" + uid);
            }
            Examinee e = new Examinee();
            e.setBatchId(batchId);
            e.setUserId(uid);
            e.setReviewStatus(0); // 初始未审核
            list.add(e);
        }
        if (list.isEmpty()) {
            return 0;
        }
        return examineeMapper.insertBatch(list);
    }

    /**
     * 8.2.3 考生检索（分页）
     */
    public PageResult<ExamineePageVO> pages(ExamineePageRequest request) {
        if (request.getParam() == null || request.getParam().getBId() == null) {
            throw new BusinessException(1, "考试批次ID不能为空");
        }
        Long batchId = request.getParam().getBId();
        String key = request.getParam().getKey();
        Integer status = request.getParam().getStatus();
        PageHelper.startPage(request.getPage(), request.getSize());
        List<Examinee> list = examineeMapper.selectByBatch(batchId, key, status);
        PageInfo<Examinee> pi = new PageInfo<>(list);
        List<ExamineePageVO> vos = new ArrayList<>();
        for (Examinee e : list) {
            AccountUser u = accountUserMapper.selectById(e.getUserId());
            if (u == null) {
                continue;
            }
            ExamineePageVO vo = new ExamineePageVO();
            vo.setUserId(u.getId());
            vo.setUserName(u.getName());
            vo.setPhone(u.getPhone());
            vo.setOrg(u.getOrgName());
            vo.setOrgId(u.getOrgId());
            vo.setVerifiedStatus(e.getReviewStatus());
            vo.setIdNo(u.getIdNo());
            vos.add(vo);
        }
        PageInfo<ExamineePageVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    /**
     * 8.2.4 移除考生
     */
    @Transactional(rollbackFor = Exception.class)
    public int removeExaminees(Long batchId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(1, "请选择要移除的考生");
        }
        return examineeMapper.deleteByBatchAndUserIds(batchId, userIds);
    }

    /**
     * 8.2.5 可添加考生列表（分页）
     */
    public PageResult<ExamineePageVO> optionalPages(ExamineeOptionalPageRequest request) {
        if (request.getParam() == null || request.getParam().getBId() == null) {
            throw new BusinessException(1, "考试批次ID不能为空");
        }
        Long batchId = request.getParam().getBId();
        String key = request.getParam().getKey();
        // 先分页查出符合条件的 userId 列表
        PageHelper.startPage(request.getPage(), request.getSize());
        List<Long> userIds = examineeMapper.selectOptionalUserIds(batchId, key);
        PageInfo<Long> pi = new PageInfo<>(userIds);
        List<ExamineePageVO> vos = new ArrayList<>();
        for (Long uid : userIds) {
            AccountUser u = accountUserMapper.selectById(uid);
            if (u == null) continue;
            ExamineePageVO vo = new ExamineePageVO();
            vo.setUserId(u.getId());
            vo.setUserName(u.getName());
            vo.setPhone(u.getPhone());
            vo.setOrg(u.getOrgName());
            vo.setOrgId(u.getOrgId());
            vo.setIdNo(u.getIdNo());
            vos.add(vo);
        }
        PageInfo<ExamineePageVO> voPi = new PageInfo<>(vos);
        voPi.setTotal(pi.getTotal());
        voPi.setPages(pi.getPages());
        return PageResult.of(voPi);
    }

    /**
     * 8.2.6 考试报名审核
     */
    @Transactional(rollbackFor = Exception.class)
    public void review(ExamineeReviewRequest request) {
        ExamBatch batch = examBatchMapper.selectById(request.getBatchId());
        if (batch == null) {
            throw new BusinessException(1, "考试批次不存在");
        }
        int status = Boolean.TRUE.equals(request.getRs()) ? 1 : -1;
        examineeMapper.updateReviewStatus(request.getBatchId(), request.getIds(), status);
    }
}


