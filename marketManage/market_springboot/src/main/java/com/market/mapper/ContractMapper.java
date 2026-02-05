package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Contract;
import com.market.entity.vo.ContractVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 租赁合同Mapper接口
 */
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
    
    /**
     * 分页查询合同列表（包含关联信息）
     */
    @Select("<script>" +
            "SELECT c.*, t.business_name as tenant_name, b.booth_no " +
            "FROM contract c " +
            "LEFT JOIN tenant t ON c.tenant_id = t.id " +
            "LEFT JOIN booth b ON c.booth_id = b.id " +
            "<where>" +
            "  <if test='contractNo != null and contractNo != \"\"'>" +
            "    AND c.contract_no LIKE CONCAT('%', #{contractNo}, '%')" +
            "  </if>" +
            "  <if test='status != null'>" +
            "    AND c.status = #{status}" +
            "  </if>" +
            "</where>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    Page<ContractVO> selectContractVOPage(Page<ContractVO> page, 
                                         @Param("contractNo") String contractNo, 
                                         @Param("status") Integer status);
    
    /**
     * 根据ID查询合同详情（包含关联信息）
     */
    @Select("SELECT c.*, t.business_name as tenant_name, b.booth_no " +
            "FROM contract c " +
            "LEFT JOIN tenant t ON c.tenant_id = t.id " +
            "LEFT JOIN booth b ON c.booth_id = b.id " +
            "WHERE c.id = #{id}")
    ContractVO selectContractVOById(@Param("id") Long id);
}

