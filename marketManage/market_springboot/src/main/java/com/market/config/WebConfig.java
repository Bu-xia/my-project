package com.market.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private UploadConfig uploadConfig;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源访问
        registry.addResourceHandler(uploadConfig.getUrlPrefix() + "**")
                .addResourceLocations("file:" + uploadConfig.getPath());
        
        System.out.println("静态资源映射配置: " + uploadConfig.getUrlPrefix() + "** -> file:" + uploadConfig.getPath());
        System.out.println("支持的文件类型: avatars, documents 等");
    }
}
