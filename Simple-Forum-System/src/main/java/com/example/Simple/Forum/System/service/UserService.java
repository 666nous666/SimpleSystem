package com.example.Simple.Forum.System.service;

import com.example.Simple.Forum.System.model.User;
import java.util.Map;
import java.util.Optional;

// 用户服务接口：定义用户相关的业务方法
public interface UserService {
    // 用户登录验证
    Map<String, Object> login(String username, String password);

    // 用户注册
    Map<String, Object> register(String username, String password, int level);

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 获取用户权限等级
    int getUserLevel(String username);
}