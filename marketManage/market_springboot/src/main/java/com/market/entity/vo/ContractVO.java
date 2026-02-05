package com.market.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同视图对象，包含关联的租户和摊位信息
 */
@Data
public class ContractVO {
    
    private Long id;
    private String contractNo;
    private Long tenantId;
    private String tenantName;  // 租户名称（商户名称）
    private Long boothId;
    private String boothNo;     // 摊位编号
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    private Integer status;
    private String contractFile;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
