package com.market.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.vo.RentalVO;
import com.market.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 我的租赁信息Controller
 */
@RestController
@RequestMapping("/api/user/rental")
public class RentalController {
    
    @Autowired
    private RentalService rentalService;
    
    /**
     * 获取当前租赁信息（包含详细信息）
     */
    @GetMapping("/current")
    public Result<List<RentalVO>> getCurrentRental(@RequestParam Long userId) {
        List<RentalVO> list = rentalService.getCurrentRentalWithDetails(userId);
        return Result.success(list);
    }
    
    /**
     * 获取历史租赁信息（分页，包含详细信息）
     */
    @GetMapping("/history")
    public Result<PageResult<RentalVO>> getHistoryRental(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId) {
        Page<RentalVO> page = rentalService.getHistoryRentalWithDetails(current, size, userId);
        PageResult<RentalVO> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取租赁详情（包含详细信息）
     */
    @GetMapping("/detail/{id}")
    public Result<RentalVO> getRentalDetail(@PathVariable Long id) {
        RentalVO rental = rentalService.getRentalDetailById(id);
        if (rental != null) {
            return Result.success(rental);
        }
        return Result.error("租赁信息不存在");
    }
    
    /**
     * 申请续租
     */
    @PostMapping("/renew/{id}")
    public Result<String> renewRental(@PathVariable Long id) {
        // 简化实现，实际应该创建新的租赁申请
        return Result.success("续租申请已提交");
    }
}

