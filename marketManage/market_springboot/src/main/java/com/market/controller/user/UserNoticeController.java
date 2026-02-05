package com.market.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.Notice;
import com.market.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端公告查看Controller
 */
@RestController
@RequestMapping("/api/user/notice")
public class UserNoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取公告列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Notice> page = noticeService.getNoticePage(current, size, keyword, null, 1);
        PageResult<Notice> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取公告详情（并增加阅读量）
     */
    @GetMapping("/detail/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice != null) {
            // 增加阅读量
            noticeService.increaseViewCount(id);
            // 更新notice对象中的阅读量
            notice.setViewCount((notice.getViewCount() == null ? 0 : notice.getViewCount()) + 1);
            return Result.success(notice);
        }
        return Result.error("公告不存在");
    }
    
    /**
     * 获取置顶公告
     */
    @GetMapping("/top")
    public Result<List<Notice>> getTopNotices() {
        List<Notice> list = noticeService.getTopNotices();
        return Result.success(list);
    }
}

