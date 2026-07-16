package com.travel.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.ai.entity.TravelStrategy;
import com.travel.ai.mapper.TravelStrategyMapper;
import com.travel.ai.service.TravelStrategyService;
import com.travel.ai.util.AiApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelStrategyServiceImpl extends ServiceImpl<TravelStrategyMapper, TravelStrategy> implements TravelStrategyService {

    @Autowired
    private AiApiUtil aiApiUtil;

    @Override
    public TravelStrategy generateStrategy(Long userId, String startPoint, String endPoint,
                                           Integer days, String budget, String preference) {
        String aiContent = aiApiUtil.generateTravelStrategy(startPoint, endPoint, days, budget, preference);
        
        TravelStrategy strategy = new TravelStrategy();
        strategy.setUserId(userId != null ? userId : 0L);
        strategy.setStartPoint(startPoint);
        strategy.setEndPoint(endPoint);
        strategy.setTravelDays(days);
        strategy.setBudget(budget);
        strategy.setPreference(preference);
        strategy.setAiContent(aiContent != null ? aiContent : "AI生成失败，请稍后重试");
        
        baseMapper.insert(strategy);
        return strategy;
    }

    @Override
    public List<TravelStrategy> listByUserId(Long userId) {
        LambdaQueryWrapper<TravelStrategy> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(TravelStrategy::getUserId, userId);
        }
        wrapper.orderByDesc(TravelStrategy::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}