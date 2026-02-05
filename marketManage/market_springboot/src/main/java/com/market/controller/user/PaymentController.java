package com.market.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.vo.PaymentVO;
import com.market.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 费用查询与支付Controller
 */
@RestController
@RequestMapping("/api/user/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 获取待支付账单（分页，包含详细信息）
     */
    @GetMapping("/unpaid")
    public Result<PageResult<PaymentVO>> getUnpaidPayments(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId) {
        Page<PaymentVO> page = paymentService.getUnpaidPaymentsWithDetails(current, size, userId);
        PageResult<PaymentVO> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取已支付账单（分页，包含详细信息）
     */
    @GetMapping("/paid")
    public Result<PageResult<PaymentVO>> getPaidPayments(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId) {
        Page<PaymentVO> page = paymentService.getPaidPaymentsWithDetails(current, size, userId);
        PageResult<PaymentVO> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 支付账单
     */
    @PostMapping("/pay")
    public Result<String> payBill(@RequestParam Long id, @RequestParam String paymentMethod) {
        if (paymentService.payBill(id, paymentMethod)) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败");
    }
    
    /**
     * 获取账单详情（包含详细信息）
     */
    @GetMapping("/detail/{id}")
    public Result<PaymentVO> getPaymentDetail(@PathVariable Long id) {
        PaymentVO payment = paymentService.getPaymentDetailById(id);
        if (payment != null) {
            return Result.success(payment);
        }
        return Result.error("账单不存在");
    }
}

