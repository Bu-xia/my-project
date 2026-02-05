package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.Notice;
import com.market.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告与通知管理Controller
 */
@RestController
@RequestMapping("/api/admin/notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取公告列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer noticeType,
            @RequestParam(required = false) Integer status) {
        
        Page<Notice> page = noticeService.getNoticePage(current, size, keyword, noticeType, status);
        PageResult<Notice> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 发布公告
     */
    @PostMapping("/add")
    public Result<String> addNotice(@RequestBody Notice notice) {
        if (noticeService.addNotice(notice)) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/update")
    public Result<String> updateNotice(@RequestBody Notice notice) {
        if (noticeService.updateNotice(notice)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        if (noticeService.deleteNotice(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
    
    /**
     * 置顶公告
     */
    @PutMapping("/top/{id}")
    public Result<String> topNotice(@PathVariable Long id) {
        if (noticeService.topNotice(id)) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }
}

