package com.travel.ai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.travel.ai.common.Result;
import com.travel.ai.util.AiApiUtil;
import com.travel.ai.util.GaodeApiUtil;
import com.travel.ai.util.WeatherApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/map")
public class MapController {

    @Autowired
    private GaodeApiUtil gaodeApiUtil;
    
    @Autowired
    private WeatherApiUtil weatherApiUtil;
    
    @Autowired
    private AiApiUtil aiApiUtil;

    @GetMapping("/geocode")
    public Result<Map<String, Object>> geocode(@RequestParam String address) {
        Map<String, Object> result = gaodeApiUtil.geocode(address);
        if (result == null) {
            return Result.error("地址解析失败");
        }
        return Result.success(result);
    }

    @GetMapping("/route")
    public Result<Map<String, Object>> route(@RequestParam String origin, 
                                             @RequestParam String destination,
                                             @RequestParam(defaultValue = "driving") String type) {
        Map<String, Object> result = gaodeApiUtil.route(origin, destination, type);
        if (result == null) {
            return Result.error("路线规划失败");
        }
        return Result.success(result);
    }

    @GetMapping("/search")
    public Result<List<Map<String, Object>>> search(@RequestParam String location,
                                                    @RequestParam(defaultValue = "景点") String keyword,
                                                    @RequestParam(defaultValue = "5000") Integer radius) {
        List<Map<String, Object>> result = gaodeApiUtil.searchNearby(location, keyword, radius);
        if (result == null) {
            return Result.error("搜索失败");
        }
        return Result.success(result);
    }

    @GetMapping("/hotels")
    public Result<List<Map<String, Object>>> searchHotels(@RequestParam String location,
                                                          @RequestParam(defaultValue = "3000") Integer radius) {
        List<Map<String, Object>> result = gaodeApiUtil.searchNearby(location, "酒店", radius);
        if (result == null) {
            return Result.error("搜索失败");
        }
        return Result.success(result);
    }

    @GetMapping("/weather")
    public Result<Map<String, Object>> weather(@RequestParam String city) {
        Map<String, Object> result = weatherApiUtil.getWeather(city);
        if (result == null) {
            return Result.error("天气查询失败");
        }
        return Result.success(result);
    }

    @GetMapping("/route/ai")
    public Result<Map<String, Object>> aiRoute(@RequestParam String origin, 
                                               @RequestParam String destination) {
        String aiResponse = aiApiUtil.getRouteDescription(origin, destination);
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
                resultMap.put("origin", json.getString("origin"));
                resultMap.put("destination", json.getString("destination"));
                resultMap.put("totalDistance", json.getString("totalDistance"));
                resultMap.put("totalDuration", json.getString("totalDuration"));
                resultMap.put("steps", json.getJSONArray("steps"));
                return Result.success(resultMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Map<String, Object> fallbackResult = new HashMap<>();
        fallbackResult.put("origin", origin);
        fallbackResult.put("destination", destination);
        fallbackResult.put("totalDistance", "未知");
        fallbackResult.put("totalDuration", "未知");
        fallbackResult.put("steps", null);
        return Result.success(fallbackResult);
    }
}