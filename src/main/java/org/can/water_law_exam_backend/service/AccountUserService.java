package org.can.water_law_exam_backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserAddRequest;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserPageRequest;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserUpdateRequest;
import org.can.water_law_exam_backend.dto.response.accountuser.AccountUserVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.entity.AccountUser;
import org.can.water_law_exam_backend.entity.Role;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.AccountUserMapper;
import org.can.water_law_exam_backend.mapper.RoleMapper;
import org.can.water_law_exam_backend.mapper.UserRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUserService {

    private final AccountUserMapper accountUserMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 添加学员
     *
     * @param request 添加请求
     * @return 新添加学员的ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addAccountUser(AccountUserAddRequest request) {
        // 检查身份证号是否已存在
        AccountUser existingUser = accountUserMapper.selectByIdNo(request.getIdNo());
        if (existingUser != null) {
            throw new BusinessException(1, "身份证号已存在");
        }

        // 检查手机号是否已存在
        existingUser = accountUserMapper.selectByPhone(request.getPhone());
        if (existingUser != null) {
            throw new BusinessException(1, "手机号已存在");
        }

        // 确定初始密码
        String password;
        if (request.getLast6digits()) {
            // 使用身份证号后6位作为初始密码
            password = request.getIdNo().substring(request.getIdNo().length() - 6);
        } else {
            // 使用pwd参数作为初始密码
            if (request.getPwd() == null || request.getPwd().trim().isEmpty()) {
                throw new BusinessException(1, "密码不能为空");
            }
            password = request.getPwd();
        }

        // 创建学员对象
        AccountUser accountUser = new AccountUser();
        accountUser.setName(request.getName().trim());
        accountUser.setOrgId(request.getOrgId());
        accountUser.setIdNo(request.getIdNo().trim());
        accountUser.setPhone(request.getPhone().trim());
        accountUser.setPwd(passwordEncoder.encode(password));
        accountUser.setLocked(false);

        // 插入数据库
        int rows = accountUserMapper.insert(accountUser);
        if (rows == 0) {
            throw new BusinessException(1, "添加学员失败");
        }

        log.info("添加学员成功：id={}, name={}, idNo={}", accountUser.getId(), accountUser.getName(), accountUser.getIdNo());
        return accountUser.getId();
    }

    /**
     * 修改学员信息
     *
     * @param request 修改请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountUser(AccountUserUpdateRequest request) {
        // 检查学员是否存在
        AccountUser accountUser = accountUserMapper.selectById(request.getId());
        if (accountUser == null) {
            throw new BusinessException(1, "学员不存在");
        }

        // 检查身份证号是否与其他学员重复
        AccountUser existingUser = accountUserMapper.selectByIdNo(request.getIdNo());
        if (existingUser != null && !existingUser.getId().equals(request.getId())) {
            throw new BusinessException(1, "身份证号已存在");
        }

        // 检查手机号是否与其他学员重复
        existingUser = accountUserMapper.selectByPhone(request.getPhone());
        if (existingUser != null && !existingUser.getId().equals(request.getId())) {
            throw new BusinessException(1, "手机号已存在");
        }

        // 更新基本信息
        accountUser.setId(request.getId());
        accountUser.setName(request.getName().trim());
        accountUser.setOrgId(request.getOrgId());
        accountUser.setIdNo(request.getIdNo().trim());
        accountUser.setPhone(request.getPhone().trim());

        // 处理密码更新
        if (request.getLast6digits()) {
            // 使用身份证号后6位作为密码
            String password = request.getIdNo().substring(request.getIdNo().length() - 6);
            accountUser.setPwd(passwordEncoder.encode(password));
        } else if (request.getPwd() != null && !request.getPwd().trim().isEmpty()) {
            // 使用pwd参数作为密码
            accountUser.setPwd(passwordEncoder.encode(request.getPwd()));
        } else {
            // 不更新密码
            accountUser.setPwd(null);
        }

        // 更新数据库
        int rows = accountUserMapper.update(accountUser);
        if (rows == 0) {
            throw new BusinessException(1, "修改学员信息失败");
        }

        log.info("修改学员信息成功：id={}, name={}, idNo={}", accountUser.getId(), accountUser.getName(), accountUser.getIdNo());
    }

    /**
     * 批量删除学员
     *
     * @param ids 学员ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccountUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(1, "请选择要删除的学员");
        }

        // 检查每个学员是否存在
        for (Long id : ids) {
            AccountUser accountUser = accountUserMapper.selectById(id);
            if (accountUser == null) {
                throw new BusinessException(1, "学员ID " + id + " 不存在");
            }
        }

        // 删除学员关联的角色
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }

        // 批量删除学员
        int rows = accountUserMapper.deleteBatch(ids);
        if (rows == 0) {
            throw new BusinessException(1, "批量删除学员失败");
        }

        log.info("批量删除学员成功：删除数量={}", rows);
    }

    /**
     * "禁用|取消禁用"学员
     *
     * @param id 学员ID
     * @return 设置成功后的用户状态：true-禁用，false-正常
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean toggleLock(Long id) {
        // 检查学员是否存在
        AccountUser accountUser = accountUserMapper.selectById(id);
        if (accountUser == null) {
            throw new BusinessException(1, "学员不存在");
        }

        // 切换禁用状态
        int rows = accountUserMapper.toggleLock(id);
        if (rows == 0) {
            throw new BusinessException(1, "切换学员状态失败");
        }

        // 返回切换后的状态
        AccountUser updatedUser = accountUserMapper.selectById(id);
        Boolean newStatus = updatedUser.getLocked();

        log.info("切换学员状态成功：id={}, name={}, locked={}", id, accountUser.getName(), newStatus);
        return newStatus;
    }

    /**
     * 获取学员信息列表-分页
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    public PageResult<AccountUserVO> getAccountUsersByPage(AccountUserPageRequest request) {
        // 获取查询参数
        Long orgId = null;
        String key = null;
        if (request.getParam() != null) {
            orgId = request.getParam().getOrg();
            if (request.getParam().getKey() != null) {
                key = request.getParam().getKey().trim();
                if (key.isEmpty()) {
                    key = null;
                }
            }
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(request.getPage(), request.getSize());
        List<AccountUser> list = accountUserMapper.selectByPage(orgId, key);
        PageInfo<AccountUser> pageInfo = new PageInfo<>(list);

        // 转换为VO
        List<AccountUserVO> voList = list.stream().map(accountUser -> {
            AccountUserVO vo = new AccountUserVO();
            BeanUtils.copyProperties(accountUser, vo);
            
            // 查询用户的角色列表
            List<Role> roles = roleMapper.selectByUserId(accountUser.getId());
            vo.setRoles(roles.stream().map(Role::getRoleName).collect(Collectors.toList()));
            
            return vo;
        }).collect(Collectors.toList());

        // 转换为PageResult（使用转换后的VO列表）
        return PageResult.of(pageInfo, voList);
    }

    /**
     * 获取单个学员基本信息
     *
     * @param id 学员ID
     * @return 学员信息
     */
    public AccountUserVO getAccountUserById(Long id) {
        AccountUser accountUser = accountUserMapper.selectById(id);
        if (accountUser == null) {
            throw new BusinessException(1, "学员不存在");
        }

        AccountUserVO vo = new AccountUserVO();
        BeanUtils.copyProperties(accountUser, vo);
        
        // 查询用户的角色列表
        List<Role> roles = roleMapper.selectByUserId(accountUser.getId());
        vo.setRoles(roles.stream().map(Role::getRoleName).collect(Collectors.toList()));

        return vo;
    }
}

