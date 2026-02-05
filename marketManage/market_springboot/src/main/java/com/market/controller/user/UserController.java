package com.market.controller.user;

import com.market.common.Result;
import com.market.config.UploadConfig;
import com.market.entity.User;
import com.market.service.UserService;
import com.market.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 用户个人中心Controller
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UploadConfig uploadConfig;
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody User user) {
        // 不允许更新密码和用户名
        user.setPassword(null);
        user.setUsername(null);
        
        if (userService.updateUser(user)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                     @RequestParam("userId") Long userId) {
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
            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                if (userService.updateUser(user)) {
                    System.out.println("头像上传成功:");
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
                return Result.error("用户不存在");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 通用文件上传接口（身份证件等）
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam(value = "type", defaultValue = "document") String type) {
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
            
            // 检查文件大小 (5MB，比头像更大一些)
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("文件大小不能超过5MB，当前文件大小：" + FileUploadUtil.formatFileSize(file.getSize()));
            }
            
            // 根据类型确定保存目录
            String uploadDir = uploadConfig.getPath() + type + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成唯一文件名
            String fileName = FileUploadUtil.generateFileName(originalFilename);
            
            // 保存文件
            File savedFile = FileUploadUtil.saveFile(file, uploadDir, fileName);
            
            // 返回访问URL
            String fileUrl = uploadConfig.getUrlPrefix() + type + "/" + fileName;
            
            System.out.println("文件上传成功:");
            System.out.println("用户ID: " + userId);
            System.out.println("文件类型: " + type);
            System.out.println("原始文件名: " + originalFilename);
            System.out.println("保存文件名: " + fileName);
            System.out.println("文件大小: " + FileUploadUtil.formatFileSize(file.getSize()));
            System.out.println("保存路径: " + savedFile.getAbsolutePath());
            System.out.println("访问URL: " + fileUrl);
            
            return Result.success(fileUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            // 获取用户信息
            User user = userService.getById(request.getUserId());
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 验证旧密码（这里简化处理，实际应该加密比较）
            if (!user.getPassword().equals(request.getOldPassword())) {
                return Result.error("当前密码不正确");
            }
            
            // 更新新密码（这里简化处理，实际应该加密存储）
            user.setPassword(request.getNewPassword());
            if (userService.updateUser(user)) {
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
        private Long userId;
        private String oldPassword;
        private String newPassword;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
