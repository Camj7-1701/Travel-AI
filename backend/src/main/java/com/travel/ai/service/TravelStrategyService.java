package com.travel.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.ai.entity.TravelStrategy;

import java.util.List;

public interface TravelStrategyService extends IService<TravelStrategy> {
    
    TravelStrategy generateStrategy(Long userId, String startPoint, String endPoint, 
                                     Integer days, String budget, String preference);
    
    List<TravelStrategy> listByUserId(Long userId);
}