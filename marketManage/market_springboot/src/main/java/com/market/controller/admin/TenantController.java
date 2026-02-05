package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.Tenant;
import com.market.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 租户信息管理Controller
 */
@RestController
@RequestMapping("/api/admin/tenant")
public class TenantController {
    
    @Autowired
    private TenantService tenantService;
    
    /**
     * 获取租户列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Tenant>> getTenantList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Tenant> page = tenantService.getTenantPage(current, size, keyword, status);
        PageResult<Tenant> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取租户详情
     */
    @GetMapping("/detail/{id}")
    public Result<Tenant> getTenantDetail(@PathVariable Long id) {
        Tenant tenant = tenantService.getById(id);
        if (tenant != null) {
            return Result.success(tenant);
        }
        return Result.error("租户不存在");
    }
    
    /**
     * 添加租户
     */
    @PostMapping("/add")
    public Result<String> addTenant(@RequestBody Tenant tenant) {
        if (tenantService.addTenant(tenant)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }
    
    /**
     * 更新租户信息
     */
    @PutMapping("/update")
    public Result<String> updateTenant(@RequestBody Tenant tenant) {
        if (tenantService.updateTenant(tenant)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除租户
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteTenant(@PathVariable Long id) {
        if (tenantService.deleteTenant(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}

