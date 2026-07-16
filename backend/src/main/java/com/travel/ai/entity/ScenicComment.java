package com.travel.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scenic_comment")
public class ScenicComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long scenicId;
    
    private String content;
    
    private Integer likeNum;
    
    private LocalDateTime createTime;
}