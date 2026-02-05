package com.market.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    private Long total;      // 总记录数
    private List<T> records; // 当前页数据
    private Long current;    // 当前页码
    private Long size;       // 每页条数
}

