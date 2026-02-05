package com.market.entity.vo;

import com.market.entity.Rental;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 租赁信息视图对象（包含摊位、合同详细信息）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RentalVO extends Rental {
    
    // 摊位相关信息
    private String boothNumber;      // 摊位编号
    private String boothName;        // 摊位名称
    private String location;         // 位置
    private Double area;             // 面积
    
    // 合同相关信息
    private String contractNo;       // 合同编号
    private BigDecimal monthlyRent;  // 月租金
    private BigDecimal deposit;      // 押金
    private Integer duration;        // 租期（月）
    private BigDecimal totalAmount;  // 总费用
    
    // 租户相关信息
    private String tenantName;       // 租户名称
    private String contactPerson;    // 联系人
    private String contactPhone;     // 联系电话
}

