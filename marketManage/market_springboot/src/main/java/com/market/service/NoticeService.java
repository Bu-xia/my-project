package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Notice;

import java.util.List;

/**
 * 公告通知Service接口
 */
public interface NoticeService {
    
    /**
     * 分页查询公告列表
     */
    Page<Notice> getNoticePage(int current, int size);
    
    /**
     * 分页查询公告列表（带筛选条件）
     */
    Page<Notice> getNoticePage(int current, int size, String keyword, Integer noticeType, Integer status);
    
    /**
     * 根据ID获取公告详情
     */
    Notice getById(Long id);
    
    /**
     * 发布公告
     */
    boolean addNotice(Notice notice);
    
    /**
     * 更新公告
     */
    boolean updateNotice(Notice notice);
    
    /**
     * 删除公告
     */
    boolean deleteNotice(Long id);
    
    /**
     * 置顶公告
     */
    boolean topNotice(Long id);
    
    /**
     * 获取置顶公告
     */
    List<Notice> getTopNotices();
    
    /**
     * 增加公告浏览量
     */
    boolean increaseViewCount(Long id);
}

