package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.vo.BoothApplyVO;
import com.market.service.BoothApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-摊位申请管理Controller
 */
@RestController
@RequestMapping("/api/admin/apply")
public class AdminApplyController {
    
    @Autowired
    private BoothApplyService boothApplyService;
    
    /**
     * 获取申请列表（分页，包含摊位和用户信息）
     */
    @GetMapping("/list")
    public Result<PageResult<BoothApplyVO>> getApplyList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<BoothApplyVO> page = boothApplyService.getApplyPageWithDetails(current, size, status, keyword);
        PageResult<BoothApplyVO> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取申请详情
     */
    @GetMapping("/detail/{id}")
    public Result<BoothApplyVO> getApplyDetail(@PathVariable Long id) {
        BoothApplyVO apply = boothApplyService.getApplyDetailById(id);
        if (apply != null) {
            return Result.success(apply);
        }
        return Result.error("申请不存在");
    }
    
    /**
     * 审核申请
     */
    @PostMapping("/audit")
    public Result<String> auditApply(@RequestBody AuditRequest request) {
        System.out.println("==================== 收到申请审核请求 ====================");
        System.out.println("申请ID: " + request.getId());
        System.out.println("审核状态: " + request.getStatus());
        System.out.println("拒绝原因: " + request.getRejectReason());
        System.out.println("========================================================");
        
        try {
            if (boothApplyService.auditApply(request.getId(), request.getStatus(), request.getRejectReason())) {
                String message = request.getStatus() == 1 ? "申请审核通过" : "申请已拒绝";
                System.out.println(message + "，申请ID: " + request.getId());
                return Result.success(message);
            }
            System.err.println("申请审核失败");
            return Result.error("审核失败");
        } catch (Exception e) {
            System.err.println("申请审核异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("审核失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取申请统计信息
     */
    @GetMapping("/statistics")
    public Result<ApplyStatistics> getApplyStatistics() {
        ApplyStatistics stats = boothApplyService.getApplyStatistics();
        return Result.success(stats);
    }
    
    /**
     * 审核请求体
     */
    public static class AuditRequest {
        private Long id;
        private Integer status; // 1-通过，2-拒绝
        private String rejectReason; // 拒绝原因
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }
    
    /**
     * 申请统计信息
     */
    public static class ApplyStatistics {
        private Long totalCount;        // 总申请数
        private Long pendingCount;      // 待审核数
        private Long approvedCount;     // 已通过数
        private Long rejectedCount;     // 已拒绝数
        private Long cancelledCount;    // 已取消数
        
        public ApplyStatistics(Long totalCount, Long pendingCount, Long approvedCount, Long rejectedCount, Long cancelledCount) {
            this.totalCount = totalCount;
            this.pendingCount = pendingCount;
            this.approvedCount = approvedCount;
            this.rejectedCount = rejectedCount;
            this.cancelledCount = cancelledCount;
        }
        
        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
        public Long getApprovedCount() { return approvedCount; }
        public void setApprovedCount(Long approvedCount) { this.approvedCount = approvedCount; }
        public Long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }
        public Long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(Long cancelledCount) { this.cancelledCount = cancelledCount; }
    }
}