package com.market.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.BoothApply;
import com.market.entity.vo.BoothApplyVO;
import com.market.service.BoothApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 摊位租赁申请Controller
 */
@RestController
@RequestMapping("/api/user/apply")
public class ApplyController {
    
    @Autowired
    private BoothApplyService boothApplyService;
    
    /**
     * 提交申请
     */
    @PostMapping("/submit")
    public Result<String> submitApply(@RequestBody BoothApply apply) {
        System.out.println("==================== 收到申请提交请求 ====================");
        System.out.println("用户ID: " + apply.getUserId());
        System.out.println("摊位ID: " + apply.getBoothId());
        System.out.println("租赁月数: " + apply.getLeaseMonths());
        System.out.println("开始日期: " + apply.getStartDate());
        System.out.println("经营类型: " + apply.getBusinessType());
        System.out.println("联系电话: " + apply.getContactPhone());
        System.out.println("申请原因: " + apply.getApplyReason());
        System.out.println("经营计划: " + apply.getBusinessPlan());
        System.out.println("经营描述: " + apply.getBusinessDescription());
        System.out.println("身份证件: " + apply.getIdCardImages());
        System.out.println("========================================================");
        
        try {
            if (boothApplyService.submitApply(apply)) {
                System.out.println("申请提交成功，申请ID: " + apply.getId());
                return Result.success("提交成功");
            }
            System.err.println("申请提交失败");
            return Result.error("提交失败");
        } catch (Exception e) {
            System.err.println("申请提交异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("提交失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我的申请列表（分页，包含摊位信息）
     */
    @GetMapping("/myList")
    public Result<PageResult<BoothApplyVO>> getMyApplyList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId) {
        Page<BoothApplyVO> page = boothApplyService.getMyApplyPageWithBooth(current, size, userId);
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
    public Result<BoothApply> getApplyDetail(@PathVariable Long id) {
        BoothApply apply = boothApplyService.getById(id);
        if (apply != null) {
            return Result.success(apply);
        }
        return Result.error("申请不存在");
    }
    
    /**
     * 取消申请
     */
    @DeleteMapping("/cancel/{id}")
    public Result<String> cancelApply(@PathVariable Long id) {
        if (boothApplyService.cancelApply(id)) {
            return Result.success("取消成功");
        }
        return Result.error("取消失败");
    }
}

