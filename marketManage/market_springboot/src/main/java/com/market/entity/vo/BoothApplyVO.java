package com.market.entity.vo;

import com.market.entity.BoothApply;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 摊位申请视图对象（包含摊位详细信息）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BoothApplyVO extends BoothApply {
    
    // 摊位相关信息
    private String boothNo;      // 摊位编号
    private String boothName;    // 摊位名称
    private String location;     // 位置
    private Double area;         // 面积
    private Double price;        // 租金
    private String boothDescription; // 摊位描述
    
    // 用户相关信息
    private String username;     // 用户名
    private String realName;     // 真实姓名
    private String phone;        // 联系电话
    private String email;        // 邮箱
}

