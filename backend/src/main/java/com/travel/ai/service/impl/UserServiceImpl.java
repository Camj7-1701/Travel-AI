package com.travel.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.ai.entity.User;
import com.travel.ai.mapper.UserMapper;
import com.travel.ai.service.UserService;
import com.travel.ai.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public User findByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public User register(String username, String password, String nickname) {
        if (findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setStatus(1);
        
        baseMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已禁用");
        }
        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Override
    public User updateProfile(Long userId, String nickname, String avatar, String phone) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        
        baseMapper.updateById(user);
        return user;
    }
}