package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.market.entity.SysAdmin;
import com.market.mapper.SysAdminMapper;
import com.market.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统管理员Service实现类
 */
@Service
public class SysAdminServiceImpl implements SysAdminService {
    
    @Autowired
    private SysAdminMapper sysAdminMapper;
    
    @Override
    public SysAdmin login(String username, String password) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getUsername, username)
               .eq(SysAdmin::getPassword, password)
               .eq(SysAdmin::getStatus, 1);
        return sysAdminMapper.selectOne(wrapper);
    }
    
    @Override
    public SysAdmin getById(Long id) {
        return sysAdminMapper.selectById(id);
    }
    
    @Override
    public boolean updateAdmin(SysAdmin admin) {
        return sysAdminMapper.updateById(admin) > 0;
    }
}

