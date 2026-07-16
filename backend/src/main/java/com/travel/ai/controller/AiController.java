package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.util.AiApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiApiUtil aiApiUtil;

    @PostMapping("/image")
    public Result<Map<String, Object>> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isEmpty()) {
            return Result.error("请输入图片描述");
        }
        
        String imageUrl = aiApiUtil.generateImage(prompt);
        if (imageUrl == null) {
            return Result.error("图片生成失败");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("url", imageUrl);
        return Result.success(result);
    }
}