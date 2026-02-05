package com.market.controller.admin;

import com.market.common.Result;
import com.market.config.UploadConfig;
import com.market.entity.SysAdmin;
import com.market.service.SysAdminService;
import com.market.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 管理员个人中心Controller
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private SysAdminService sysAdminService;
    
    @Autowired
    private UploadConfig uploadConfig;
    
    /**
     * 获取管理员信息
     */
    @GetMapping("/info/{id}")
    public Result<SysAdmin> getAdminInfo(@PathVariable Long id) {
        SysAdmin admin = sysAdminService.getById(id);
        if (admin != null) {
            // 不返回密码
            admin.setPassword(null);
            return Result.success(admin);
        }
        return Result.error("管理员不存在");
    }
    
    /**
     * 更新管理员信息
     */
    @PutMapping("/update")
    public Result<String> updateAdminInfo(@RequestBody SysAdmin admin) {
        try {
            // 获取原始管理员信息
            SysAdmin existingAdmin = sysAdminService.getById(admin.getId());
            if (existingAdmin == null) {
                return Result.error("管理员不存在");
            }
            
            // 只更新允许修改的字段
            existingAdmin.setRealName(admin.getRealName());
            existingAdmin.setEmail(admin.getEmail());
            existingAdmin.setPhone(admin.getPhone());
            
            // 如果有头像字段，也允许更新
            if (admin.getAvatar() != null) {
                existingAdmin.setAvatar(admin.getAvatar());
            }
            
            if (sysAdminService.updateAdmin(existingAdmin)) {
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                     @RequestParam("adminId") Long adminId) {
        try {
            // 基础验证
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
            
            // 检查文件类型
            if (!FileUploadUtil.isImageFile(file)) {
                return Result.error("只能上传图片文件");
            }
            
            // 检查文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = FileUploadUtil.getFileExtension(originalFilename);
            if (!uploadConfig.isAllowedImageType(extension)) {
                return Result.error("不支持的图片格式，支持格式：" + uploadConfig.getAllowedImageTypes());
            }
            
            // 检查文件大小 (2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB，当前文件大小：" + FileUploadUtil.formatFileSize(file.getSize()));
            }
            
            // 生成唯一文件名
            String fileName = FileUploadUtil.generateFileName(originalFilename);
            
            // 保存文件
            File savedFile = FileUploadUtil.saveFile(file, uploadConfig.getAvatarPath(), fileName);
            
            // 返回访问URL
            String avatarUrl = uploadConfig.getUrlPrefix() + "avatars/" + fileName;
            
            // 更新数据库中的头像路径
            SysAdmin admin = sysAdminService.getById(adminId);
            if (admin != null) {
                admin.setAvatar(avatarUrl);
                if (sysAdminService.updateAdmin(admin)) {
                    System.out.println("管理员头像上传成功:");
                    System.out.println("管理员ID: " + adminId);
                    System.out.println("原始文件名: " + originalFilename);
                    System.out.println("保存文件名: " + fileName);
                    System.out.println("文件大小: " + FileUploadUtil.formatFileSize(file.getSize()));
                    System.out.println("保存路径: " + savedFile.getAbsolutePath());
                    System.out.println("访问URL: " + avatarUrl);
                    System.out.println("数据库更新成功");
                    
                    return Result.success(avatarUrl);
                } else {
                    return Result.error("头像上传成功，但更新数据库失败");
                }
            } else {
                return Result.error("管理员不存在");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            // 获取管理员信息
            SysAdmin admin = sysAdminService.getById(request.getAdminId());
            if (admin == null) {
                return Result.error("管理员不存在");
            }
            
            // 验证旧密码（这里简化处理，实际应该加密比较）
            if (!admin.getPassword().equals(request.getOldPassword())) {
                return Result.error("当前密码不正确");
            }
            
            // 更新新密码（这里简化处理，实际应该加密存储）
            admin.setPassword(request.getNewPassword());
            if (sysAdminService.updateAdmin(admin)) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("密码修改失败：" + e.getMessage());
        }
    }
    
    /**
     * 密码修改请求类
     */
    public static class PasswordChangeRequest {
        private Long adminId;
        private String oldPassword;
        private String newPassword;
        
        // getters and setters
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
