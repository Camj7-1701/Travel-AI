package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.entity.User;
import com.travel.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        User user = userService.getById(userId);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        String nickname = params.get("nickname");
        String avatar = params.get("avatar");
        String phone = params.get("phone");
        
        try {
            User user = userService.updateProfile(userId, nickname, avatar, phone);
            return Result.success("更新成功", user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}