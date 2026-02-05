package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Booth;
import com.market.mapper.BoothMapper;
import com.market.service.BoothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 摊位Service实现类
 */
@Service
public class BoothServiceImpl implements BoothService {
    
    @Autowired
    private BoothMapper boothMapper;
    
    @Override
    public Page<Booth> getBoothPage(int current, int size, String keyword, Integer status, String minArea, String maxArea) {
        Page<Booth> page = new Page<>(current, size);
        LambdaQueryWrapper<Booth> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词筛选（摊位编号、摊位名称、位置）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Booth::getBoothNo, keyword)
                   .or().like(Booth::getBoothName, keyword)
                   .or().like(Booth::getLocation, keyword));
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Booth::getStatus, status);
        }
        
        // 面积范围筛选
        if (StringUtils.hasText(minArea)) {
            try {
                wrapper.ge(Booth::getArea, new BigDecimal(minArea));
            } catch (NumberFormatException e) {
                System.err.println("最小面积参数格式错误: " + minArea);
            }
        }
        if (StringUtils.hasText(maxArea)) {
            try {
                wrapper.le(Booth::getArea, new BigDecimal(maxArea));
            } catch (NumberFormatException e) {
                System.err.println("最大面积参数格式错误: " + maxArea);
            }
        }
        
        wrapper.orderByDesc(Booth::getCreateTime);
        return boothMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Booth getById(Long id) {
        return boothMapper.selectById(id);
    }
    
    @Override
    public boolean addBooth(Booth booth) {
        return boothMapper.insert(booth) > 0;
    }
    
    @Override
    public boolean updateBooth(Booth booth) {
        return boothMapper.updateById(booth) > 0;
    }
    
    @Override
    public boolean deleteBooth(Long id) {
        return boothMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<Booth> getAvailableBooths() {
        LambdaQueryWrapper<Booth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Booth::getStatus, 0);
        return boothMapper.selectList(wrapper);
    }
}

