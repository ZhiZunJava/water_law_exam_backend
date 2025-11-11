package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.can.water_law_exam_backend.dto.request.admin.AdminAddRequest;
import org.can.water_law_exam_backend.dto.request.admin.AdminPageRequest;
import org.can.water_law_exam_backend.dto.request.admin.AdminUpdateRequest;
import org.can.water_law_exam_backend.dto.response.admin.AdminVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.entity.Admin;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.AdminMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PWD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^0-9a-zA-Z]).{6,20}$");

    @Transactional(rollbackFor = Exception.class)
    public Long addAdmin(AdminAddRequest request) {
        Admin admin = new Admin();
        admin.setName(request.getName());

        // 1. 检查 user_no 是否已存在
        admin.setUserNo(request.getUserNo());
        Admin existingAdmin = adminMapper.selectByUserNo(admin.getUserNo());
        if (existingAdmin != null) {
            throw new BusinessException(1, "用户账号已存在，请更换账号");
        }

        if (StringUtils.hasText(request.getPwd())) {
            if (!PWD_PATTERN.matcher(request.getPwd()).matches()) {
                throw new BusinessException(400, "密码不符合复杂度要求");
            }
            String encoded = passwordEncoder.encode(request.getPwd());
            admin.setPwd(encoded);
        }
        admin.setLocked(Boolean.FALSE);
        int rows = adminMapper.insertAdmin(admin);
        if (rows == 0 ) {
            throw new BusinessException(1, "添加管理员失败");
        }
        return admin.getId();
    }

    public void updateAdmin(AdminUpdateRequest request) {
        // 校验账号唯一
        int dup = adminMapper.countByUserNoExcludeId(request.getUserNo(), request.getId());
        if (dup > 0) {
            throw new BusinessException(400, "用户账号已存在");
        }
        // 更新基础信息
        int r = adminMapper.updateBase(request.getId(), request.getName().trim(), request.getUserNo().trim());
        if (r == 0) {
            throw new BusinessException(404, "管理员不存在");
        }
        // 可选修改密码
        if (StringUtils.hasText(request.getPwd())) {
            if (!PWD_PATTERN.matcher(request.getPwd()).matches()) {
                throw new BusinessException(400, "密码不符合复杂度要求");
            }
            String encoded = passwordEncoder.encode(request.getPwd());
            adminMapper.updatePassword(request.getId(), encoded);
        }
    }

    public PageResult<AdminVO> getAdminsByPage(AdminPageRequest request) {
        String key = null;
        if (request.getParam() != null && StringUtils.hasText(request.getParam().getKey())) {
            key = request.getParam().getKey().trim();
        }
        PageHelper.startPage(request.getPage(), request.getSize());
        List<Admin> list = adminMapper.selectByPage(key);
        PageInfo<Admin> pageInfo = new PageInfo<>(list);
        List<AdminVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(pageInfo, voList);
    }

    public void deleteAdmins(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的数据");
        }
        adminMapper.deleteBatch(ids);
    }

    public boolean toggleEnabled(Long id) {
        if(id == 1) {
            throw new BusinessException(1, "超级管理员不允许禁用");
        }

        int r = adminMapper.toggleLocked(id);
        if (r == 0) {
            throw new BusinessException(404, "管理员不存在");
        }
        Admin admin = adminMapper.selectById(id);
        return Boolean.FALSE.equals(admin.getLocked());
    }

    public AdminVO getAdminById(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        return toVO(admin);
    }

    private AdminVO toVO(Admin admin) {
        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setName(admin.getName());
        vo.setUserNo(admin.getUserNo());
        vo.setEnabled(Boolean.FALSE.equals(admin.getLocked()));
        vo.setPwd(null);
        return vo;
    }
}


