package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Notice;
import com.market.mapper.NoticeMapper;
import com.market.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告通知Service实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    @Override
    public Page<Notice> getNoticePage(int current, int size) {
        return getNoticePage(current, size, null, null, null);
    }
    
    @Override
    public Page<Notice> getNoticePage(int current, int size, String keyword, Integer noticeType, Integer status) {
        Page<Notice> page = new Page<>(current, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 标题关键词筛选
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Notice::getTitle, keyword.trim());
        }
        
        // 公告类型筛选
        if (noticeType != null) {
            wrapper.eq(Notice::getNoticeType, noticeType);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Notice::getStatus, status);
        }
        // 如果没有指定状态筛选，默认查询所有状态（管理员应该能看到所有状态的公告）
        
        // 排序：置顶优先，然后按发布时间倒序
        wrapper.orderByDesc(Notice::getIsTop)
               .orderByDesc(Notice::getPublishTime);
               
        return noticeMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }
    
    @Override
    public boolean addNotice(Notice notice) {
        notice.setPublishTime(LocalDateTime.now());
        notice.setViewCount(0);
        notice.setStatus(1);
        return noticeMapper.insert(notice) > 0;
    }
    
    @Override
    public boolean updateNotice(Notice notice) {
        return noticeMapper.updateById(notice) > 0;
    }
    
    @Override
    public boolean deleteNotice(Long id) {
        return noticeMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean topNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            notice.setIsTop(notice.getIsTop() == 1 ? 0 : 1);
            return noticeMapper.updateById(notice) > 0;
        }
        return false;
    }
    
    @Override
    public List<Notice> getTopNotices() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getIsTop, 1)
               .eq(Notice::getStatus, 1)
               .orderByDesc(Notice::getPublishTime);
        return noticeMapper.selectList(wrapper);
    }
    
    @Override
    public boolean increaseViewCount(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            int currentCount = notice.getViewCount() == null ? 0 : notice.getViewCount();
            notice.setViewCount(currentCount + 1);
            return noticeMapper.updateById(notice) > 0;
        }
        return false;
    }
}

