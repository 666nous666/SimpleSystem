package com.example.Simple.Forum.System.controller;

import com.example.Simple.Forum.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 认证控制器：处理用户登录、注册等请求
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")  // 允许前端跨域访问
public class AuthController {

    @Autowired
    private UserService userService;

    // 登录接口：验证用户名和密码
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        return userService.login(username, password);
    }

    // 注册接口：创建新用户
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> registerRequest) {
        String username = (String) registerRequest.get("username");
        String password = (String) registerRequest.get("password");
        Integer level = (Integer) registerRequest.getOrDefault("level", 2);  // 默认注册为普通用户

        return userService.register(username, password, level);
    }

    // 获取所有用户列表（测试接口）
    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        // 这里应该有权限验证，简单起见直接返回
        return Map.of(
                "success", true,
                "users", userService.findByUsername("")  // 实际应该返回所有用户
        );
    }
}