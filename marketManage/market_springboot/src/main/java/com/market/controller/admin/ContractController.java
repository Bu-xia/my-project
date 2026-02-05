package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.vo.ContractVO;
import com.market.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 租赁合同管理Controller
 */
@RestController
@RequestMapping("/api/admin/contract")
public class ContractController {
    
    @Autowired
    private ContractService contractService;
    
    /**
     * 获取合同列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<ContractVO>> getContractList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) Integer status) {
        Page<ContractVO> page = contractService.getContractPage(current, size, contractNo, status);
        PageResult<ContractVO> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取合同详情
     */
    @GetMapping("/detail/{id}")
    public Result<ContractVO> getContractDetail(@PathVariable Long id) {
        ContractVO contract = contractService.getContractVOById(id);
        if (contract != null) {
            return Result.success(contract);
        }
        return Result.error("合同不存在");
    }
    
    /**
     * 更新合同状态
     */
    @PutMapping("/updateStatus")
    public Result<String> updateStatus(@RequestBody ContractStatusRequest request) {
        if (contractService.updateStatus(request.getId(), request.getStatus())) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 合同状态更新请求体
     */
    public static class ContractStatusRequest {
        private Long id;
        private Integer status;
        private String remark;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}

