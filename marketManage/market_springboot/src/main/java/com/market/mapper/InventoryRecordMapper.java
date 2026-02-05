package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.entity.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物资出入库记录Mapper接口
 */
@Mapper
public interface InventoryRecordMapper extends BaseMapper<InventoryRecord> {
}

