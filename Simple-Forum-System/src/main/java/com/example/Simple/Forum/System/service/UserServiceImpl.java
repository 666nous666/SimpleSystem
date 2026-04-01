package com.example.Simple.Forum.System.service;

import com.example.Simple.Forum.System.model.*;
import com.example.Simple.Forum.System.repository.UserRepository;
import com.example.Simple.Forum.System.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 用户服务实现类：处理用户登录、注册等业务逻辑
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // 处理用户登录：验证用户名和密码，返回用户信息和权限
    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查找用户
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "账号不存在");
            result.put("code", 404);
            return result;
        }

        User user = userOpt.get();

        // 2. 验证密码（使用 SHA-256+ 盐值加密）
        boolean passwordValid = PasswordUtil.verifyPassword(
                password,
                user.getPasswordHash(),
                user.getSalt()
        );

        if (!passwordValid) {
            result.put("success", false);
            result.put("message", "密码错误");
            result.put("code", 401);
            return result;
        }

        // 3. 登录成功
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("code", 200);
        result.put("username", user.getUsername());
        result.put("level", user.getLevel());

        // 根据权限等级返回不同的角色信息
        switch (user.getLevel()) {
            case 0:
                result.put("role", "系统管理员");
                result.put("permissions", ((Admin) user).canManageUsers() ?
                        "管理用户、管理论坛、查看日志、指派版主" : "");
                break;
            case 1:
                result.put("role", "版主");
                result.put("permissions", ((Moderator) user).canDeletePost() ?
                        "删除帖子、置顶帖子、禁言用户" : "");
                break;
            default:
                result.put("role", "普通用户");
                result.put("permissions", ((OrdinaryUser) user).canEditOwnPost() ?
                        "发帖、评论、浏览、编辑自己的帖子" : "");
                break;
        }

        return result;
    }

    // 处理用户注册：创建新用户并保存到数据库
    @Override
    public Map<String, Object> register(String username, String password, int level) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            result.put("code", 409);
            return result;
        }

        // 2. 密码强度检查（简单示例）
        if (password.length() < 6) {
            result.put("success", false);
            result.put("message", "密码长度至少 6 位");
            result.put("code", 400);
            return result;
        }

        // 3. 创建新用户（加密密码）
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        User newUser;
        switch (level) {
            case 0:
                newUser = new Admin(username, passwordHash, salt);
                break;
            case 1:
                newUser = new Moderator(username, passwordHash, salt);
                break;
            default:
                newUser = new OrdinaryUser(username, passwordHash, salt);
                break;
        }

        // 4. 保存用户
        boolean saved = userRepository.save(newUser);

        if (saved) {
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("code", 201);
            result.put("username", username);
            result.put("level", level);
        } else {
            result.put("success", false);
            result.put("message", "注册失败，请稍后重试");
            result.put("code", 500);
        }

        return result;
    }

    // 根据用户名查找用户
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 获取用户的权限等级
    @Override
    public int getUserLevel(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(User::getLevel).orElse(-1);
    }
}