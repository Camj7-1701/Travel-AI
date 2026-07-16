package com.travel.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("travel_strategy")
public class TravelStrategy {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String startPoint;
    
    private String endPoint;
    
    private Integer travelDays;
    
    private String budget;
    
    private String preference;
    
    private String aiContent;
    
    private LocalDateTime createTime;
}