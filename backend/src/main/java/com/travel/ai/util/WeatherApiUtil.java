package com.travel.ai.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherApiUtil {

    @Autowired
    private AiApiUtil aiApiUtil;

    public Map<String, Object> getWeather(String city) {
        Map<String, Object> result = fetchWeatherFromAi(city);
        if (result != null) {
            return result;
        }
        return getMockWeather(city);
    }

    private Map<String, Object> fetchWeatherFromAi(String city) {
        try {
            String aiResponse = aiApiUtil.getWeather(city);
            if (aiResponse != null && !aiResponse.isEmpty()) {
                aiResponse = aiResponse.trim();
                if (aiResponse.startsWith("```json")) {
                    aiResponse = aiResponse.substring(7, aiResponse.length() - 3).trim();
                } else if (aiResponse.startsWith("```")) {
                    aiResponse = aiResponse.substring(3, aiResponse.length() - 3).trim();
                }
                
                JSONObject json = JSON.parseObject(aiResponse);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("city", json.getString("city"));
                resultMap.put("weather", json.getString("weather"));
                resultMap.put("temperature", json.getString("temperature"));
                resultMap.put("windDirection", json.getString("windDirection"));
                resultMap.put("windPower", json.getString("windPower"));
                resultMap.put("humidity", json.getString("humidity"));
                resultMap.put("clothingAdvice", json.getString("clothingAdvice"));
                resultMap.put("reportTime", java.time.LocalDateTime.now().toString().replace("T", " "));
                
                JSONArray forecast = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    JSONObject day = new JSONObject();
                    day.put("date", java.time.LocalDate.now().plusDays(i).toString());
                    day.put("dayweather", getRandomWeather());
                    day.put("nightweather", getRandomWeather());
                    day.put("daytemp", String.valueOf(24 + (int)(Math.random() * 6)));
                    day.put("nighttemp", String.valueOf(16 + (int)(Math.random() * 6)));
                    forecast.add(day);
                }
                resultMap.put("forecast", forecast);
                
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private Map<String, Object> getMockWeather(String city) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("city", city);
        resultMap.put("weather", "晴");
        resultMap.put("temperature", "26");
        resultMap.put("windDirection", "东北风");
        resultMap.put("windPower", "3");
        resultMap.put("humidity", "65");
        resultMap.put("reportTime", java.time.LocalDateTime.now().toString().replace("T", " "));
        
        JSONArray forecast = new JSONArray();
        for (int i = 0; i < 5; i++) {
            JSONObject day = new JSONObject();
            day.put("date", java.time.LocalDate.now().plusDays(i).toString());
            day.put("dayweather", getRandomWeather());
            day.put("nightweather", getRandomWeather());
            day.put("daytemp", String.valueOf(24 + (int)(Math.random() * 6)));
            day.put("nighttemp", String.valueOf(16 + (int)(Math.random() * 6)));
            forecast.add(day);
        }
        resultMap.put("forecast", forecast);
        resultMap.put("clothingAdvice", getClothingAdvice(resultMap));
        
        return resultMap;
    }

    private String getRandomWeather() {
        String[] weathers = {"晴", "多云", "阴", "小雨", "中雨", "雷阵雨"};
        return weathers[(int)(Math.random() * weathers.length)];
    }

    private String getClothingAdvice(Map<String, Object> weather) {
        String temperature = (String) weather.get("temperature");
        String weatherDesc = (String) weather.get("weather");
        
        if (temperature == null) {
            return "天气信息暂未获取到";
        }
        
        try {
            int temp = Integer.parseInt(temperature);
            StringBuilder advice = new StringBuilder();
            
            if (temp >= 32) {
                advice.append("☀️ 天气炎热，建议穿短袖、短裤、凉鞋，注意防晒和补水。");
            } else if (temp >= 28) {
                advice.append("🌤️ 天气偏热，建议穿短袖、薄长裤、透气鞋，可带一把遮阳伞。");
            } else if (temp >= 24) {
                advice.append("🌥️ 天气舒适，建议穿薄长袖或短袖T恤，搭配薄长裤。");
            } else if (temp >= 20) {
                advice.append("⛅ 天气微凉，建议穿长袖衬衫或薄外套，搭配长裤。");
            } else if (temp >= 15) {
                advice.append("☁️ 天气稍凉，建议穿薄毛衣或厚外套，注意早晚温差。");
            } else if (temp >= 10) {
                advice.append("🌧️ 天气较冷，建议穿毛衣+外套或薄羽绒服，注意保暖。");
            } else if (temp >= 5) {
                advice.append("❄️ 天气寒冷，建议穿厚羽绒服、毛衣、保暖裤，注意防寒。");
            } else {
                advice.append("🥶 天气严寒，建议穿厚羽绒服、保暖内衣、棉裤，注意防冻。");
            }
            
            if (weatherDesc != null) {
                if (weatherDesc.contains("雨")) {
                    advice.append(" 记得携带雨伞或雨衣。");
                } else if (weatherDesc.contains("雪")) {
                    advice.append(" 路面可能湿滑，注意防滑。");
                } else if (weatherDesc.contains("晴")) {
                    advice.append(" 阳光充足，注意防晒。");
                } else if (weatherDesc.contains("阴")) {
                    advice.append(" 天气阴沉，可适当增加衣物。");
                }
            }
            
            return advice.toString();
        } catch (NumberFormatException e) {
            return "天气信息暂未获取到";
        }
    }
}