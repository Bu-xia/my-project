package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Contract;
import com.market.entity.vo.ContractVO;

/**
 * 租赁合同Service接口
 */
public interface ContractService {
    
    /**
     * 分页查询合同列表（包含关联信息）
     */
    Page<ContractVO> getContractPage(int current, int size, String contractNo, Integer status);
    
    /**
     * 根据ID获取合同详情（包含关联信息）
     */
    ContractVO getContractVOById(Long id);
    
    /**
     * 根据ID获取合同详情（基础实体）
     */
    Contract getById(Long id);
    
    /**
     * 更新合同状态
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 添加合同
     */
    boolean addContract(Contract contract);
}

