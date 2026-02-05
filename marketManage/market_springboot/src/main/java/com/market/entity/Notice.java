package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告通知实体类
 */
@Data
@TableName("notice")
public class Notice {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Integer noticeType;
    private Integer isTop;
    private String coverImage;
    private String publisher;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

