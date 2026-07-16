package com.travel.ai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.travel.ai.common.Result;
import com.travel.ai.entity.Scenic;
import com.travel.ai.service.ScenicService;
import com.travel.ai.util.AiApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scenic")
public class ScenicController {

    @Autowired
    private ScenicService scenicService;
    
    @Autowired
    private AiApiUtil aiApiUtil;

    @GetMapping("/list")
    public Result<List<Scenic>> list(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) String keyword) {
        List<Scenic> list = scenicService.listByCategoryAndKeyword(category, keyword);
        return Result.success(list);
    }

    @GetMapping("/hot")
    public Result<List<Scenic>> hot(@RequestParam(defaultValue = "6") Integer limit) {
        List<Scenic> list = scenicService.listHot(limit);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result<Scenic> detail(@PathVariable Long id) {
        Scenic scenic = scenicService.getDetail(id);
        if (scenic == null) {
            return Result.error("景点不存在");
        }
        return Result.success(scenic);
    }

    @GetMapping("/ai-info")
    public Result<Map<String, Object>> getAiInfo(@RequestParam String scenicName) {
        String aiResponse = aiApiUtil.getScenicInfo(scenicName);
        if (aiResponse != null && !aiResponse.isEmpty()) {
            aiResponse = aiResponse.trim();
            if (aiResponse.startsWith("```json")) {
                aiResponse = aiResponse.substring(7, aiResponse.length() - 3).trim();
            } else if (aiResponse.startsWith("```")) {
                aiResponse = aiResponse.substring(3, aiResponse.length() - 3).trim();
            }
            
            try {
                JSONObject json = JSON.parseObject(aiResponse);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("name", json.getString("name"));
                resultMap.put("description", json.getString("description"));
                resultMap.put("features", json.getJSONArray("features"));
                resultMap.put("tips", json.getJSONArray("tips"));
                resultMap.put("bestTime", json.getString("bestTime"));
                resultMap.put("duration", json.getString("duration"));
                return Result.success(resultMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Map<String, Object> fallbackResult = new HashMap<>();
        fallbackResult.put("name", scenicName);
        fallbackResult.put("description", "暂无详细介绍");
        fallbackResult.put("features", null);
        fallbackResult.put("tips", null);
        fallbackResult.put("bestTime", "暂无信息");
        fallbackResult.put("duration", "暂无信息");
        return Result.success(fallbackResult);
    }
}