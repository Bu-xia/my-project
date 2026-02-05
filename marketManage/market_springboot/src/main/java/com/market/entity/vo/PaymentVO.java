package com.market.entity.vo;

import com.market.entity.Payment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付记录视图对象（包含关联信息）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentVO extends Payment {
    
    // 摊位相关信息
    private String boothNo;          // 摊位编号
    private String boothName;        // 摊位名称
    private String location;         // 位置
    
    // 合同相关信息
    private String contractNo;       // 合同编号
    
    // 用户相关信息
    private String username;         // 用户名
    private String realName;         // 真实姓名
}

