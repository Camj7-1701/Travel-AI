package com.travel.ai.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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
public class AiApiUtil {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.base-url}")
    private String baseUrl;

    @Value("${ai.model}")
    private String model;

    public String generateImage(String prompt) {
        String url = baseUrl + "images/generations";
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("size", "1024x1024");
        
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(body))
                    .timeout(60000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject json = JSON.parseObject(response.body());
                JSONArray data = json.getJSONArray("data");
                if (data != null && !data.isEmpty()) {
                    return data.getJSONObject(0).getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public String getScenicInfo(String scenicName) {
        String url = baseUrl + "chat/completions";
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位专业的旅游向导，请提供详细的景点信息。回复格式必须为JSON格式，包含name、description、features、tips、bestTime、duration字段。");
        messages.add(systemMsg);
        
        Map<String, Object> userMsg = new HashMap<>();
        String prompt = String.format("请提供【%s】的详细景点信息。请返回JSON格式，包含以下字段：\n" +
                "1. name: 景点名称\n" +
                "2. description: 景点详细介绍（200-300字）\n" +
                "3. features: 特色亮点数组（3-5个）\n" +
                "4. tips: 旅游贴士数组（3-5个）\n" +
                "5. bestTime: 最佳游览时间\n" +
                "6. duration: 建议游览时长\n" +
                "只返回JSON数据，不要包含其他文字。", scenicName);
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        body.put("temperature", 0.5);
        body.put("max_tokens", 2000);
        
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(body))
                    .timeout(60000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject json = JSON.parseObject(response.body());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        return message.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public String getRouteDescription(String origin, String destination) {
        String url = baseUrl + "chat/completions";
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位专业的导航助手，请提供详细的文字导航路线描述。回复格式必须为JSON格式，包含origin、destination、totalDistance、totalDuration、steps字段。");
        messages.add(systemMsg);
        
        Map<String, Object> userMsg = new HashMap<>();
        String prompt = String.format("请提供从【%s】到【%s】的文字导航路线。请返回JSON格式，包含以下字段：\n" +
                "1. origin: 起点名称\n" +
                "2. destination: 终点名称\n" +
                "3. totalDistance: 总距离（如'15.5公里'）\n" +
                "4. totalDuration: 总时间（如'35分钟'）\n" +
                "5. steps: 导航步骤数组，每个步骤包含instruction（导航指令）、distance（距离）、duration（时间）\n" +
                "请提供详细的导航步骤，包含主要道路名称和转弯指示。只返回JSON数据，不要包含其他文字。", origin, destination);
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        body.put("temperature", 0.5);
        body.put("max_tokens", 2000);
        
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(body))
                    .timeout(60000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject json = JSON.parseObject(response.body());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        return message.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public String getWeather(String city) {
        String url = baseUrl + "chat/completions";
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位专业的天气助手，请提供准确的天气信息和实用的穿衣建议。回复格式必须为JSON格式，包含city、weather、temperature、windDirection、windPower、humidity、clothingAdvice字段。");
        messages.add(systemMsg);
        
        Map<String, Object> userMsg = new HashMap<>();
        String prompt = String.format("请提供【%s】今天的天气信息。请返回JSON格式，包含以下字段：\n" +
                "1. city: 城市名称\n" +
                "2. weather: 天气状况（如晴、多云、小雨等）\n" +
                "3. temperature: 当前温度（数字，单位摄氏度）\n" +
                "4. windDirection: 风向\n" +
                "5. windPower: 风力等级\n" +
                "6. humidity: 湿度百分比\n" +
                "7. clothingAdvice: 根据天气给出的穿衣建议\n" +
                "只返回JSON数据，不要包含其他文字。", city);
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        body.put("temperature", 0.3);
        body.put("max_tokens", 1000);
        
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(body))
                    .timeout(60000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject json = JSON.parseObject(response.body());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        return message.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public String generateTravelStrategy(String startPoint, String endPoint, Integer days, 
                                          String budget, String preference) {
        String url = baseUrl + "chat/completions";
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        
        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位专业的旅游规划师，擅长根据用户需求生成详细的旅游攻略。请用中文回复，内容要丰富实用，格式清晰。");
        messages.add(systemMsg);
        
        Map<String, Object> userMsg = new HashMap<>();
        String prompt = String.format("请为我生成一份从【%s】到【%s】的旅游攻略，出行天数：%d天，预算：%s，偏好：%s。\n" +
                "请包含以下内容：\n" +
                "1. 交通方案（飞机/高铁/自驾）\n" +
                "2. 每日行程安排（上午、下午、晚上）\n" +
                "3. 当地美食推荐\n" +
                "4. 住宿建议\n" +
                "5. 避雷提示和注意事项\n" +
                "6. 必去景点打卡清单\n" +
                "请用清晰的标题和分段，让攻略易于阅读。", 
                startPoint, endPoint, days, budget, preference);
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 4000);
        
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(body))
                    .timeout(120000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject json = JSON.parseObject(response.body());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        return message.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}