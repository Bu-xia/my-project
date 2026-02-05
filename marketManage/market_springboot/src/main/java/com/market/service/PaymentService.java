package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Payment;
import com.market.entity.vo.PaymentVO;

/**
 * 支付记录Service接口
 */
public interface PaymentService {
    
    /**
     * 获取待支付账单
     */
    Page<Payment> getUnpaidPayments(int current, int size, Long userId);
    
    /**
     * 获取待支付账单（包含详细信息）
     */
    Page<PaymentVO> getUnpaidPaymentsWithDetails(int current, int size, Long userId);
    
    /**
     * 获取已支付账单
     */
    Page<Payment> getPaidPayments(int current, int size, Long userId);
    
    /**
     * 获取已支付账单（包含详细信息）
     */
    Page<PaymentVO> getPaidPaymentsWithDetails(int current, int size, Long userId);
    
    /**
     * 根据ID获取账单详情
     */
    Payment getById(Long id);
    
    /**
     * 根据ID获取账单详情（包含详细信息）
     */
    PaymentVO getPaymentDetailById(Long id);
    
    /**
     * 支付账单
     */
    boolean payBill(Long id, String paymentMethod);
    
    /**
     * 创建支付记录
     */
    boolean createPayment(Payment payment);
}

