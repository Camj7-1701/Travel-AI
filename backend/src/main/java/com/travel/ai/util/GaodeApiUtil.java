package com.travel.ai.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GaodeApiUtil {

    @Value("${gaode.web-service-key}")
    private String webServiceKey;

    public Map<String, Object> geocode(String address) {
        String url = "https://restapi.amap.com/v3/geocode/geo";
        Map<String, Object> params = new HashMap<>();
        params.put("key", webServiceKey);
        params.put("address", address);
        String result = HttpUtil.get(url, params);
        JSONObject json = JSON.parseObject(result);
        if ("1".equals(json.getString("status"))) {
            JSONArray geocodes = json.getJSONArray("geocodes");
            if (!geocodes.isEmpty()) {
                JSONObject geocode = geocodes.getJSONObject(0);
                String location = geocode.getString("location");
                String[] coords = location.split(",");
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("longitude", coords[0]);
                resultMap.put("latitude", coords[1]);
                resultMap.put("address", geocode.getString("formatted_address"));
                return resultMap;
            }
        }
        return null;
    }

    public Map<String, Object> route(String origin, String destination, String type) {
        String originCoord = origin;
        String destCoord = destination;
        
        try {
            if (!origin.contains(",") || !origin.matches("^[0-9.]+,[0-9.]+$")) {
                Map<String, Object> originGeo = geocode(origin);
                if (originGeo != null) {
                    originCoord = originGeo.get("longitude") + "," + originGeo.get("latitude");
                } else {
                    return null;
                }
            }
            
            if (!destination.contains(",") || !destination.matches("^[0-9.]+,[0-9.]+$")) {
                Map<String, Object> destGeo = geocode(destination);
                if (destGeo != null) {
                    destCoord = destGeo.get("longitude") + "," + destGeo.get("latitude");
                } else {
                    return null;
                }
            }
            
            String url = "https://restapi.amap.com/v3/direction/driving";
            Map<String, Object> params = new HashMap<>();
            params.put("key", webServiceKey);
            params.put("origin", originCoord);
            params.put("destination", destCoord);
            String result = HttpUtil.get(url, params);
            JSONObject json = JSON.parseObject(result);
            
            if ("1".equals(json.getString("status"))) {
                JSONObject route = json.getJSONObject("route");
                if (route != null && route.getJSONArray("paths") != null && !route.getJSONArray("paths").isEmpty()) {
                    JSONObject path = route.getJSONArray("paths").getJSONObject(0);
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("distance", path.getString("distance"));
                    resultMap.put("duration", path.getString("duration"));
                    resultMap.put("originCoord", originCoord);
                    resultMap.put("destCoord", destCoord);
                    
                    JSONArray steps = path.getJSONArray("steps");
                    if (steps != null && !steps.isEmpty()) {
                        List<Map<String, Object>> stepList = new ArrayList<>();
                        for (int i = 0; i < steps.size(); i++) {
                            JSONObject step = steps.getJSONObject(i);
                            Map<String, Object> stepMap = new HashMap<>();
                            stepMap.put("instruction", step.getString("instruction"));
                            stepMap.put("polyline", step.getString("polyline"));
                            stepMap.put("distance", step.getString("distance"));
                            stepMap.put("duration", step.getString("duration"));
                            stepList.add(stepMap);
                        }
                        resultMap.put("steps", stepList);
                    }
                    
                    return resultMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> searchNearby(String location, String keyword, int radius) {
        String url = "https://restapi.amap.com/v3/place/around";
        Map<String, Object> params = new HashMap<>();
        params.put("key", webServiceKey);
        params.put("location", location);
        params.put("keywords", keyword);
        params.put("radius", radius);
        params.put("offset", 20);
        params.put("output", "json");
        try {
            String result = HttpUtil.get(url, params);
            System.out.println("Gaode search result: " + result);
            JSONObject json = JSON.parseObject(result);
            if ("1".equals(json.getString("status"))) {
                JSONArray pois = json.getJSONArray("pois");
                List<Map<String, Object>> resultList = new ArrayList<>();
                for (int i = 0; i < pois.size(); i++) {
                    JSONObject poi = pois.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", poi.getString("name"));
                    map.put("address", poi.getString("address"));
                    map.put("location", poi.getString("location"));
                    map.put("type", poi.getString("type"));
                    resultList.add(map);
                }
                return resultList;
            } else {
                System.out.println("Gaode search error: " + json.getString("info"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}