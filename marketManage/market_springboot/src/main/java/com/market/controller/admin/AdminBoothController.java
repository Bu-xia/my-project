package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.Booth;
import com.market.service.BoothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端摊位管理Controller
 */
@RestController
@RequestMapping("/api/admin/booth")
public class AdminBoothController {
    
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
            @RequestParam(required = false) Integer status) {
        Page<Booth> page = boothService.getBoothPage(current, size, keyword, status, null, null);
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
     * 添加摊位
     */
    @PostMapping("/add")
    public Result<String> addBooth(@RequestBody Booth booth) {
        if (boothService.addBooth(booth)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }
    
    /**
     * 更新摊位信息
     */
    @PutMapping("/update")
    public Result<String> updateBooth(@RequestBody Booth booth) {
        if (boothService.updateBooth(booth)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除摊位
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteBooth(@PathVariable Long id) {
        if (boothService.deleteBooth(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}

