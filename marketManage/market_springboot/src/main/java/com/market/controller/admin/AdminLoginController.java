package com.market.controller.admin;

import com.market.common.Result;
import com.market.entity.SysAdmin;
import com.market.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员登录Controller
 */
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {
    
    @Autowired
    private SysAdminService sysAdminService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<SysAdmin> login(@RequestParam String username, @RequestParam String password) {
        SysAdmin admin = sysAdminService.login(username, password);
        if (admin != null) {
            // 不返回密码
            admin.setPassword(null);
            return Result.success("登录成功", admin);
        }
        return Result.error("用户名或密码错误");
    }
}

