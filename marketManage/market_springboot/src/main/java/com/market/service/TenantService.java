package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Tenant;

/**
 * 租户Service接口
 */
public interface TenantService {
    
    /**
     * 分页查询租户列表
     */
    Page<Tenant> getTenantPage(int current, int size, String keyword);
    
    /**
     * 分页查询租户列表（带状态筛选）
     */
    Page<Tenant> getTenantPage(int current, int size, String keyword, Integer status);
    
    /**
     * 根据ID获取租户
     */
    Tenant getById(Long id);
    
    /**
     * 添加租户
     */
    boolean addTenant(Tenant tenant);
    
    /**
     * 更新租户
     */
    boolean updateTenant(Tenant tenant);
    
    /**
     * 删除租户
     */
    boolean deleteTenant(Long id);
}

