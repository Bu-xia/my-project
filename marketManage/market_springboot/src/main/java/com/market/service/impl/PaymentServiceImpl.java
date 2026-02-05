package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.FinanceRecord;
import com.market.entity.Payment;
import com.market.entity.vo.PaymentVO;
import com.market.mapper.FinanceRecordMapper;
import com.market.mapper.PaymentMapper;
import com.market.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 支付记录Service实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    
    @Override
    public Page<Payment> getUnpaidPayments(int current, int size, Long userId) {
        Page<Payment> page = new Page<>(current, size);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getUserId, userId)
               .eq(Payment::getStatus, 0) // 待支付
               .orderByDesc(Payment::getCreateTime);
        return paymentMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Page<PaymentVO> getUnpaidPaymentsWithDetails(int current, int size, Long userId) {
        Page<PaymentVO> page = new Page<>(current, size);
        return paymentMapper.selectUnpaidPaymentsWithDetails(page, userId);
    }
    
    @Override
    public Page<Payment> getPaidPayments(int current, int size, Long userId) {
        Page<Payment> page = new Page<>(current, size);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getUserId, userId)
               .eq(Payment::getStatus, 1) // 已支付
               .orderByDesc(Payment::getPaymentTime);
        return paymentMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Page<PaymentVO> getPaidPaymentsWithDetails(int current, int size, Long userId) {
        Page<PaymentVO> page = new Page<>(current, size);
        return paymentMapper.selectPaidPaymentsWithDetails(page, userId);
    }
    
    @Override
    public Payment getById(Long id) {
        return paymentMapper.selectById(id);
    }
    
    @Override
    public PaymentVO getPaymentDetailById(Long id) {
        return paymentMapper.selectPaymentDetailById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payBill(Long id, String paymentMethod) {
        // 1. 获取支付信息
        Payment paymentInfo = paymentMapper.selectById(id);
        if (paymentInfo == null) {
            throw new RuntimeException("支付记录不存在");
        }
        
        // 2. 更新支付状态
        Payment payment = new Payment();
        payment.setId(id);
        payment.setStatus(1); // 已支付
        payment.setPaymentMethod(getPaymentMethodName(paymentMethod));
        payment.setPaymentTime(LocalDateTime.now());
        
        int updateResult = paymentMapper.updateById(payment);
        if (updateResult <= 0) {
            throw new RuntimeException("更新支付状态失败");
        }
        
        // 3. 创建收支记录
        createFinanceRecord(paymentInfo, paymentMethod);
        
        System.out.println("支付成功，已创建收支记录，支付ID: " + id);
        return true;
    }
    
    /**
     * 创建收支记录
     */
    private void createFinanceRecord(Payment payment, String paymentMethod) {
        FinanceRecord record = new FinanceRecord();
        
        // 生成记录编号
        String recordNo = generateRecordNo();
        record.setRecordNo(recordNo);
        
        // 收入类型
        record.setRecordType(1); // 1-收入，2-支出
        
        // 根据支付类型设置分类
        String category = getFinanceCategory(payment.getPaymentType());
        record.setCategory(category);
        
        // 金额
        record.setAmount(payment.getAmount());
        
        // 关联支付ID
        record.setPaymentId(payment.getId());
        
        // 相关人员（用户ID）
        record.setRelatedPerson("用户" + payment.getUserId());
        
        // 描述
        String description = category + "收入";
        record.setDescription(description);
        
        // 记录日期
        record.setRecordDate(LocalDate.now());
        
        // 保存
        int result = financeRecordMapper.insert(record);
        if (result <= 0) {
            throw new RuntimeException("创建收支记录失败");
        }
        
        System.out.println("已创建收支记录，记录编号: " + recordNo);
    }
    
    /**
     * 生成记录编号
     */
    private String generateRecordNo() {
        return "FR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * 获取支付方式名称
     */
    private String getPaymentMethodName(String method) {
        if ("alipay".equals(method)) {
            return "支付宝";
        } else if ("wechat".equals(method)) {
            return "微信支付";
        } else if ("bank".equals(method)) {
            return "银行卡";
        }
        return method;
    }
    
    /**
     * 根据支付类型获取财务分类
     */
    private String getFinanceCategory(Integer paymentType) {
        if (paymentType == null) {
            return "其他收入";
        }
        
        switch (paymentType) {
            case 1:
                return "租金收入";
            case 2:
                return "押金收入";
            case 3:
                return "水电费收入";
            case 4:
                return "管理费收入";
            default:
                return "其他收入";
        }
    }
    
    @Override
    public boolean createPayment(Payment payment) {
        return paymentMapper.insert(payment) > 0;
    }
}

