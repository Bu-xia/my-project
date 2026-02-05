package com.market.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "market.upload")
public class UploadConfig {
    
    /**
     * 文件上传根路径
     */
    private String path;
    
    /**
     * 访问路径前缀
     */
    private String urlPrefix;
    
    /**
     * 头像上传路径
     */
    private String avatarPath;
    
    /**
     * 允许的图片格式
     */
    private String allowedImageTypes;
    
    /**
     * 允许的图片格式列表
     */
    private List<String> allowedImageTypeList;
    
    /**
     * 初始化方法，创建上传目录
     */
    @PostConstruct
    public void init() {
        // 创建上传根目录
        createDirectory(path);
        
        // 创建头像上传目录
        createDirectory(avatarPath);
        
        // 解析允许的图片格式
        if (allowedImageTypes != null && !allowedImageTypes.isEmpty()) {
            allowedImageTypeList = Arrays.asList(allowedImageTypes.split(","));
        }
        
        System.out.println("文件上传配置初始化完成:");
        System.out.println("上传根路径: " + path);
        System.out.println("头像路径: " + avatarPath);
        System.out.println("允许的图片格式: " + allowedImageTypeList);
    }
    
    /**
     * 创建目录
     */
    private void createDirectory(String dirPath) {
        if (dirPath != null && !dirPath.isEmpty()) {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    System.out.println("创建目录成功: " + dirPath);
                } else {
                    System.err.println("创建目录失败: " + dirPath);
                }
            } else {
                System.out.println("目录已存在: " + dirPath);
            }
        }
    }
    
    /**
     * 检查文件扩展名是否被允许
     */
    public boolean isAllowedImageType(String extension) {
        if (allowedImageTypeList == null || extension == null) {
            return false;
        }
        return allowedImageTypeList.contains(extension.toLowerCase());
    }
}
