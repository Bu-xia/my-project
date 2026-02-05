package com.market.service;

import com.market.entity.SysAdmin;

/**
 * 系统管理员Service接口
 */
public interface SysAdminService {
    
    /**
     * 管理员登录
     */
    SysAdmin login(String username, String password);
    
    /**
     * 根据ID获取管理员信息
     */
    SysAdmin getById(Long id);
    
    /**
     * 更新管理员信息
     */
    boolean updateAdmin(SysAdmin admin);
}

