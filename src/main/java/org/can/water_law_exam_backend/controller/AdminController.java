package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.admin.AdminAddRequest;
import org.can.water_law_exam_backend.dto.request.admin.AdminPageRequest;
import org.can.water_law_exam_backend.dto.request.admin.AdminUpdateRequest;
import org.can.water_law_exam_backend.dto.response.admin.AdminVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/adm")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 10.1 添加管理员信息
     * POST /adm/add
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody AdminAddRequest request) {
        Long id = adminService.addAdmin(request);
        return Result.success("成功添加管理员账户！", String.valueOf(id));
    }

    /**
     * 10.2 修改管理员信息
     * POST /adm/update
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<String> update(@Valid @RequestBody AdminUpdateRequest request) {
        adminService.updateAdmin(request);
        return Result.success("成功修改管理员账户");
    }

    /**
     * 10.3 管理员列表（分页）
     * GET /adm/pages
     * 采用JSON Body或者QueryParam两种方式均可。
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pages")
    public Result<PageResult<AdminVO>> pages(@RequestBody(required = false) AdminPageRequest body,
                                             @RequestParam(value = "page", required = false) Integer page,
                                             @RequestParam(value = "size", required = false) Integer size,
                                             @RequestParam(value = "key", required = false) String key) {
        AdminPageRequest req = body != null ? body : new AdminPageRequest();
        if (body == null) {
            req.setPage(page != null ? page : 1);
            req.setSize(size != null ? size : 10);
            AdminPageRequest.Param p = new AdminPageRequest.Param();
            p.setKey(key);
            req.setParam(p);
        }
        PageResult<AdminVO> result = adminService.getAdminsByPage(req);
        return Result.success(result);
    }

    /**
     * 10.4 删除管理员（批量）
     * POST /adm/delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody List<Long> ids) {
        adminService.deleteAdmins(ids);
        return Result.success("成功删除了管理员账户");
    }

    /**
     * 10.5 禁用 / 启用管理员
     * POST /adm/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public Result<Boolean> toggle(@PathVariable Long id) {
        boolean enabled = adminService.toggleEnabled(id);
        String msg = enabled ? "成功启用管理员账户" : "成功禁用管理员账户";
        return Result.success(msg, enabled);
    }

    /**
     * 10.6 获取单个管理员账户信息
     * GET /adm/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<AdminVO> getById(@PathVariable Long id) {
        AdminVO vo = adminService.getAdminById(id);
        return Result.success(vo);
    }
}


