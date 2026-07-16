package com.travel.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.ai.entity.User;

public interface UserService extends IService<User> {
    
    User findByUsername(String username);
    
    User register(String username, String password, String nickname);
    
    User login(String username, String password);
    
    User updateProfile(Long userId, String nickname, String avatar, String phone);
}