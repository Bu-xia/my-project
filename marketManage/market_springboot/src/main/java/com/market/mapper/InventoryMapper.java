package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物资仓储Mapper接口
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
}

