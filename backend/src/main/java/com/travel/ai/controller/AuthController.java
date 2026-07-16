package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.entity.User;
import com.travel.ai.service.UserService;
import com.travel.ai.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        
        try {
            User user = userService.register(username, password, nickname);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            
            return Result.success("注册成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        try {
            User user = userService.login(username, password);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            
            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}