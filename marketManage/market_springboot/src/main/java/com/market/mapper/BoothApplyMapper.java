package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.BoothApply;
import com.market.entity.vo.BoothApplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 摊位租赁申请Mapper接口
 */
@Mapper
public interface BoothApplyMapper extends BaseMapper<BoothApply> {
    
    /**
     * 分页查询用户的申请列表（联表查询摊位信息）
     */
    @Select("SELECT a.*, b.booth_no, b.booth_name, b.location, b.area, b.price " +
            "FROM booth_apply a " +
            "LEFT JOIN booth b ON a.booth_id = b.id " +
            "WHERE a.user_id = #{userId} " +
            "ORDER BY a.apply_time DESC")
    Page<BoothApplyVO> selectMyApplyPageWithBooth(Page<BoothApplyVO> page, @Param("userId") Long userId);
    
    /**
     * 分页查询所有申请列表（联表查询摊位信息）
     */
    @Select("<script>" +
            "SELECT a.*, b.booth_no, b.booth_name, b.location, b.area, b.price " +
            "FROM booth_apply a " +
            "LEFT JOIN booth b ON a.booth_id = b.id " +
            "<where>" +
            "<if test='status != null'> AND a.status = #{status}</if>" +
            "</where>" +
            "ORDER BY a.apply_time DESC" +
            "</script>")
    Page<BoothApplyVO> selectAllApplyPageWithBooth(Page<BoothApplyVO> page, @Param("status") Integer status);
    
    /**
     * 管理员查询申请列表（包含详细信息）
     */
    @Select("<script>" +
            "SELECT ba.*, b.booth_no, b.booth_name, b.location, b.price, b.area, " +
            "u.username, u.real_name, u.phone " +
            "FROM booth_apply ba " +
            "LEFT JOIN booth b ON ba.booth_id = b.id " +
            "LEFT JOIN user u ON ba.user_id = u.id " +
            "<where>" +
            "<if test='status != null'>" +
            "AND ba.status = #{status} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (b.booth_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.location LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</where>" +
            "ORDER BY ba.apply_time DESC" +
            "</script>")
    Page<BoothApplyVO> selectApplyPageWithDetails(Page<BoothApplyVO> page, 
                                                  @Param("status") Integer status, 
                                                  @Param("keyword") String keyword);
    
    /**
     * 查询申请详情
     */
    @Select("SELECT ba.*, b.booth_no, b.booth_name, b.location, b.price, b.area, b.description as booth_description, " +
            "u.username, u.real_name, u.phone, u.email " +
            "FROM booth_apply ba " +
            "LEFT JOIN booth b ON ba.booth_id = b.id " +
            "LEFT JOIN user u ON ba.user_id = u.id " +
            "WHERE ba.id = #{id}")
    BoothApplyVO selectApplyDetailById(@Param("id") Long id);
}

