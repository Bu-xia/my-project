package com.market.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.Booth;
import com.market.service.BoothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 摊位在线查找Controller
 */
@RestController
@RequestMapping("/api/user/booth")
public class BoothController {
    
    @Autowired
    private BoothService boothService;
    
    /**
     * 获取摊位列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Booth>> getBoothList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String minArea,
            @RequestParam(required = false) String maxArea) {
        Page<Booth> page = boothService.getBoothPage(current, size, keyword, status, minArea, maxArea);
        PageResult<Booth> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取摊位详情
     */
    @GetMapping("/detail/{id}")
    public Result<Booth> getBoothDetail(@PathVariable Long id) {
        Booth booth = boothService.getById(id);
        if (booth != null) {
            return Result.success(booth);
        }
        return Result.error("摊位不存在");
    }
    
    /**
     * 搜索摊位
     */
    @GetMapping("/search")
    public Result<PageResult<Booth>> searchBooth(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Booth> page = boothService.getBoothPage(current, size, keyword, null, null, null);
        PageResult<Booth> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取可租摊位列表
     */
    @GetMapping("/available")
    public Result<List<Booth>> getAvailableBooths() {
        List<Booth> list = boothService.getAvailableBooths();
        return Result.success(list);
    }
}

