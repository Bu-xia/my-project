package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Rental;
import com.market.entity.vo.RentalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 租赁信息Mapper接口
 */
@Mapper
public interface RentalMapper extends BaseMapper<Rental> {
    
    /**
     * 查询用户当前租赁信息（包含详细信息）
     */
    @Select("SELECT r.*, " +
            "b.booth_no as boothNumber, b.booth_name as boothName, b.location, b.area, " +
            "c.contract_no as contractNo, c.monthly_rent as monthlyRent, c.deposit, " +
            "TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) as duration, " +
            "(c.monthly_rent * TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) + c.deposit) as totalAmount, " +
            "t.business_name as tenantName, t.contact_person as contactPerson, t.contact_phone as contactPhone " +
            "FROM rental r " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN contract c ON r.contract_id = c.id " +
            "LEFT JOIN tenant t ON c.tenant_id = t.id " +
            "WHERE r.user_id = #{userId} AND r.status = 1 " +
            "ORDER BY r.create_time DESC")
    List<RentalVO> selectCurrentRentalWithDetails(@Param("userId") Long userId);
    
    /**
     * 分页查询用户历史租赁信息（包含详细信息）
     */
    @Select("SELECT r.*, " +
            "b.booth_no as boothNumber, b.booth_name as boothName, b.location, b.area, " +
            "c.contract_no as contractNo, c.monthly_rent as monthlyRent, c.deposit, " +
            "TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) as duration, " +
            "(c.monthly_rent * TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) + c.deposit) as totalAmount, " +
            "t.business_name as tenantName, t.contact_person as contactPerson, t.contact_phone as contactPhone " +
            "FROM rental r " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN contract c ON r.contract_id = c.id " +
            "LEFT JOIN tenant t ON c.tenant_id = t.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.create_time DESC")
    Page<RentalVO> selectHistoryRentalWithDetails(Page<RentalVO> page, @Param("userId") Long userId);
    
    /**
     * 查询租赁详情（包含详细信息）
     */
    @Select("SELECT r.*, " +
            "b.booth_no as boothNumber, b.booth_name as boothName, b.location, b.area, " +
            "c.contract_no as contractNo, c.monthly_rent as monthlyRent, c.deposit, " +
            "TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) as duration, " +
            "(c.monthly_rent * TIMESTAMPDIFF(MONTH, c.start_date, c.end_date) + c.deposit) as totalAmount, " +
            "t.business_name as tenantName, t.contact_person as contactPerson, t.contact_phone as contactPhone " +
            "FROM rental r " +
            "LEFT JOIN booth b ON r.booth_id = b.id " +
            "LEFT JOIN contract c ON r.contract_id = c.id " +
            "LEFT JOIN tenant t ON c.tenant_id = t.id " +
            "WHERE r.id = #{id}")
    RentalVO selectRentalDetailById(@Param("id") Long id);
}

