package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Payment;
import com.market.entity.vo.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    
    /**
     * 分页查询待支付账单（包含详细信息）
     */
    @Select("SELECT p.*, " +
            "b.booth_no as boothNo, b.booth_name as boothName, b.location, " +
            "c.contract_no as contractNo, " +
            "u.username, u.real_name as realName " +
            "FROM payment p " +
            "LEFT JOIN contract c ON p.contract_id = c.id " +
            "LEFT JOIN rental r ON c.id = r.contract_id " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} AND p.status = 0 " +
            "ORDER BY p.create_time DESC")
    Page<PaymentVO> selectUnpaidPaymentsWithDetails(Page<PaymentVO> page, @Param("userId") Long userId);
    
    /**
     * 分页查询已支付账单（包含详细信息）
     */
    @Select("SELECT p.*, " +
            "b.booth_no as boothNo, b.booth_name as boothName, b.location, " +
            "c.contract_no as contractNo, " +
            "u.username, u.real_name as realName " +
            "FROM payment p " +
            "LEFT JOIN contract c ON p.contract_id = c.id " +
            "LEFT JOIN rental r ON c.id = r.contract_id " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} AND p.status = 1 " +
            "ORDER BY p.payment_time DESC")
    Page<PaymentVO> selectPaidPaymentsWithDetails(Page<PaymentVO> page, @Param("userId") Long userId);
    
    /**
     * 查询支付详情（包含详细信息）
     */
    @Select("SELECT p.*, " +
            "b.booth_no as boothNo, b.booth_name as boothName, b.location, " +
            "c.contract_no as contractNo, " +
            "u.username, u.real_name as realName " +
            "FROM payment p " +
            "LEFT JOIN contract c ON p.contract_id = c.id " +
            "LEFT JOIN rental r ON c.id = r.contract_id " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.id = #{id}")
    PaymentVO selectPaymentDetailById(@Param("id") Long id);
}

