package com.market;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 跳蚤市场摊位管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.market.mapper")
public class MarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketApplication.class, args);
        System.out.println("========================================");
        System.out.println("跳蚤市场摊位管理系统启动成功！");
        System.out.println("========================================");
    }
}

