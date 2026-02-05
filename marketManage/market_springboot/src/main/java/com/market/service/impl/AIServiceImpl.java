package com.market.service.impl;

import com.market.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

/**
 * AI服务实现类 - 调用阿里云千问模型
 */
@Service
public class AIServiceImpl implements AIService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String API_KEY = "sk-8b18f3807f9f469e8b96c53c2ff3b0c5";
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String MODEL = "qwen-plus";
    
    // 系统提示词 - 针对跳蚤市场场景优化
    private static final String SYSTEM_PROMPT = 
        "你是跳蚤市场的AI智能助手，专门为用户提供摊位租赁、市场管理等相关咨询服务。\n" +
        "\n" +
        "市场信息：\n" +
        "- 市场名称：跳蚤市场摊位管理系统\n" +
        "- 摊位分区：A区、B区、C区、D区\n" +
        "- 营业时间：早8:00-晚20:00\n" +
        "- 租金范围：500-2200元/月（根据位置和面积）\n" +
        "- 押金：通常为1-2个月租金\n" +
        "\n" +
        "租赁流程：\n" +
        "1. 浏览可租摊位（摊位查找页面）\n" +
        "2. 提交租赁申请（填写申请表、上传身份证件）\n" +
        "3. 等待管理员审核（1-3个工作日）\n" +
        "4. 审核通过后签署合同\n" +
        "5. 支付租金和押金\n" +
        "6. 获得摊位开始经营\n" +
        "\n" +
        "支付方式：支持支付宝、微信支付、银行转账\n" +
        "\n" +
        "管理规定：\n" +
        "- 禁止销售违法违规商品\n" +
        "- 保持摊位整洁卫生\n" +
        "- 遵守营业时间\n" +
        "- 注意用电安全，禁止使用明火\n" +
        "\n" +
        "客服联系：\n" +
        "- 客服热线：400-123-4567（周一至周日 9:00-18:00）\n" +
        "- 紧急热线：138-0000-0000（24小时）\n" +
        "- 邮箱：service@market.com\n" +
        "\n" +
        "请根据以上信息，用专业、友好、简洁的方式回答用户问题。回答要准确、有针对性，必要时可以提供分步骤的指导。";
    
    @Override
    public String chat(String message) {
        try {
            System.out.println("开始调用AI API，用户消息: " + message);
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            System.out.println("构建请求体完成: " + requestBody);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            System.out.println("正在发送请求到阿里云API...");
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);
            
            System.out.println("收到响应，状态码: " + response.getStatusCode());
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = response.getBody();
                System.out.println("响应内容: " + responseBody);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, String> messageMap = (Map<String, String>) choice.get("message");
                    String content = messageMap.get("content");
                    System.out.println("提取的AI回复: " + content);
                    return content;
                }
            }
            
            System.out.println("未能从响应中提取内容");
            return "抱歉，我暂时无法回答您的问题，请联系人工客服。";
            
        } catch (Exception e) {
            System.err.println("调用AI API失败: " + e.getMessage());
            e.printStackTrace();
            
            // 返回友好的错误提示
            return "抱歉，AI服务暂时繁忙，请稍后再试。您也可以：\n" +
                   "1. 查看常见问题列表\n" +
                   "2. 联系人工客服：400-123-4567\n" +
                   "3. 发送邮件至：service@market.com";
        }
    }
}

