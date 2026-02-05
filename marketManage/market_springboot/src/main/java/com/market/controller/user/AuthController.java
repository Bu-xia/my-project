package com.market.controller.user;

import com.market.common.Result;
import com.market.entity.User;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册与登录Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (userService.register(user)) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败，用户名已存在");
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return Result.success("登录成功", user);
        }
        return Result.error("用户名或密码错误");
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("登出成功");
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        // 简化实现，实际应该验证手机号或邮箱
        return Result.success("密码重置功能待完善");
    }
}

