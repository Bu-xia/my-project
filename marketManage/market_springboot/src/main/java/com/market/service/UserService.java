package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.User;

/**
 * 用户Service接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 根据ID获取用户
     */
    User getById(Long id);
    
    /**
     * 更新用户信息
     */
    boolean updateUser(User user);
    
    /**
     * 分页查询用户列表
     */
    Page<User> getUserPage(int current, int size, String keyword);
}

