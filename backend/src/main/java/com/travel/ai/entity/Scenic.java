package com.travel.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("scenic")
public class Scenic {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String scenicName;
    
    private String address;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String category;
    
    private String intro;
    
    private BigDecimal price;
    
    private String openTime;
    
    private String imgUrls;
    
    private Integer hot;
    
    private LocalDateTime createTime;
}