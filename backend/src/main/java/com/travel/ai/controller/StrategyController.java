package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.entity.TravelStrategy;
import com.travel.ai.service.TravelStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    @Autowired
    private TravelStrategyService strategyService;

    @PostMapping("/generate")
    public Result<TravelStrategy> generate(@RequestBody Map<String, Object> params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.getPrincipal() != null && !(auth.getPrincipal() instanceof String)) {
            userId = (Long) auth.getPrincipal();
        }
        
        String startPoint = params.get("startPoint").toString();
        String endPoint = params.get("endPoint").toString();
        Integer days = Integer.valueOf(params.get("days").toString());
        String budget = params.get("budget").toString();
        String preference = params.get("preference").toString();
        
        try {
            TravelStrategy strategy = strategyService.generateStrategy(userId, startPoint, endPoint, days, budget, preference);
            return Result.success("攻略生成成功", strategy);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<TravelStrategy>> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.getPrincipal() != null && !(auth.getPrincipal() instanceof String)) {
            userId = (Long) auth.getPrincipal();
        }
        
        List<TravelStrategy> list = strategyService.listByUserId(userId);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result<TravelStrategy> detail(@PathVariable Long id) {
        TravelStrategy strategy = strategyService.getById(id);
        if (strategy == null) {
            return Result.error("攻略不存在");
        }
        return Result.success(strategy);
    }
}