package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Tenant;
import com.market.mapper.TenantMapper;
import com.market.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 租户Service实现类
 */
@Service
public class TenantServiceImpl implements TenantService {
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Override
    public Page<Tenant> getTenantPage(int current, int size, String keyword) {
        return getTenantPage(current, size, keyword, null);
    }
    
    @Override
    public Page<Tenant> getTenantPage(int current, int size, String keyword, Integer status) {
        Page<Tenant> page = new Page<>(current, size);
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词筛选（商户名称、联系人或联系电话）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Tenant::getBusinessName, keyword)
                              .or().like(Tenant::getContactPerson, keyword)
                              .or().like(Tenant::getContactPhone, keyword));
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Tenant::getStatus, status);
        }
        
        wrapper.orderByDesc(Tenant::getCreateTime);
        return tenantMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Tenant getById(Long id) {
        return tenantMapper.selectById(id);
    }
    
    @Override
    public boolean addTenant(Tenant tenant) {
        return tenantMapper.insert(tenant) > 0;
    }
    
    @Override
    public boolean updateTenant(Tenant tenant) {
        return tenantMapper.updateById(tenant) > 0;
    }
    
    @Override
    public boolean deleteTenant(Long id) {
        return tenantMapper.deleteById(id) > 0;
    }
}

