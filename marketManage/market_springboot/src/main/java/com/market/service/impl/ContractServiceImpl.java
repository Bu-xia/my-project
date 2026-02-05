package com.market.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Contract;
import com.market.entity.vo.ContractVO;
import com.market.mapper.ContractMapper;
import com.market.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 租赁合同Service实现类
 */
@Service
public class ContractServiceImpl implements ContractService {
    
    @Autowired
    private ContractMapper contractMapper;
    
    @Override
    public Page<ContractVO> getContractPage(int current, int size, String contractNo, Integer status) {
        Page<ContractVO> page = new Page<>(current, size);
        return contractMapper.selectContractVOPage(page, contractNo, status);
    }
    
    @Override
    public ContractVO getContractVOById(Long id) {
        return contractMapper.selectContractVOById(id);
    }
    
    @Override
    public Contract getById(Long id) {
        return contractMapper.selectById(id);
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Contract contract = new Contract();
        contract.setId(id);
        contract.setStatus(status);
        return contractMapper.updateById(contract) > 0;
    }
    
    @Override
    public boolean addContract(Contract contract) {
        return contractMapper.insert(contract) > 0;
    }
}

