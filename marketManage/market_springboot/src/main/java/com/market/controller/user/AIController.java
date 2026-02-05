package com.market.controller.user;

import com.market.common.Result;
import com.market.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI助手Controller
 */
@RestController
@RequestMapping("/api/user/ai")
public class AIController {
    
    @Autowired
    private AIService aiService;
    
    /**
     * AI对话接口
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> request) {
        try {
            System.out.println("收到AI对话请求: " + request);
            String message = request.get("message");
            System.out.println("用户消息: " + message);
            
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }
            
            String response = aiService.chat(message);
            System.out.println("AI响应: " + response);
            return Result.success(response);
        } catch (Exception e) {
            System.err.println("AI对话异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("AI服务暂时不可用: " + e.getMessage());
        }
    }
}

