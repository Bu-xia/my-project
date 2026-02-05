package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Rental;
import com.market.entity.vo.RentalVO;
import com.market.mapper.RentalMapper;
import com.market.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 租赁信息Service实现类
 */
@Service
public class RentalServiceImpl implements RentalService {
    
    @Autowired
    private RentalMapper rentalMapper;
    
    @Override
    public List<Rental> getCurrentRental(Long userId) {
        LambdaQueryWrapper<Rental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rental::getUserId, userId)
               .eq(Rental::getStatus, 1) // 租赁中
               .orderByDesc(Rental::getCreateTime);
        return rentalMapper.selectList(wrapper);
    }
    
    @Override
    public List<RentalVO> getCurrentRentalWithDetails(Long userId) {
        return rentalMapper.selectCurrentRentalWithDetails(userId);
    }
    
    @Override
    public Page<Rental> getHistoryRental(int current, int size, Long userId) {
        Page<Rental> page = new Page<>(current, size);
        LambdaQueryWrapper<Rental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rental::getUserId, userId)
               .in(Rental::getStatus, 2, 3) // 已到期或已退租
               .orderByDesc(Rental::getCreateTime);
        return rentalMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Page<RentalVO> getHistoryRentalWithDetails(int current, int size, Long userId) {
        Page<RentalVO> page = new Page<>(current, size);
        return rentalMapper.selectHistoryRentalWithDetails(page, userId);
    }
    
    @Override
    public Rental getById(Long id) {
        return rentalMapper.selectById(id);
    }
    
    @Override
    public RentalVO getRentalDetailById(Long id) {
        return rentalMapper.selectRentalDetailById(id);
    }
    
    @Override
    public boolean addRental(Rental rental) {
        return rentalMapper.insert(rental) > 0;
    }
}

