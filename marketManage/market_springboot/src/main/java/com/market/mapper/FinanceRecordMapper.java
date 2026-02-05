package com.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.entity.FinanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 费用收支记录Mapper接口
 */
@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {
}

