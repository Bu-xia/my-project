package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.controller.admin.AdminApplyController.ApplyStatistics;
import com.market.entity.BoothApply;
import com.market.entity.vo.BoothApplyVO;

/**
 * 摊位租赁申请Service接口
 */
public interface BoothApplyService {
    
    /**
     * 提交申请
     */
    boolean submitApply(BoothApply apply);
    
    /**
     * 获取我的申请列表
     */
    Page<BoothApply> getMyApplyPage(int current, int size, Long userId);
    
    /**
     * 获取我的申请列表（包含摊位信息）
     */
    Page<BoothApplyVO> getMyApplyPageWithBooth(int current, int size, Long userId);
    
    /**
     * 获取申请详情
     */
    BoothApply getById(Long id);
    
    /**
     * 取消申请
     */
    boolean cancelApply(Long id);
    
    /**
     * 审核申请（管理员）
     */
    boolean auditApply(Long id, Integer status, String rejectReason);
    
    /**
     * 分页查询所有申请（管理员）
     */
    Page<BoothApply> getAllApplyPage(int current, int size, Integer status);
    
    /**
     * 分页查询所有申请（管理员，包含摊位信息）
     */
    Page<BoothApplyVO> getAllApplyPageWithBooth(int current, int size, Integer status);
    
    /**
     * 管理员-获取申请列表（包含详细信息）
     */
    Page<BoothApplyVO> getApplyPageWithDetails(int current, int size, Integer status, String keyword);
    
    /**
     * 管理员-获取申请详情
     */
    BoothApplyVO getApplyDetailById(Long id);
    
    /**
     * 获取申请统计信息
     */
    ApplyStatistics getApplyStatistics();
}

