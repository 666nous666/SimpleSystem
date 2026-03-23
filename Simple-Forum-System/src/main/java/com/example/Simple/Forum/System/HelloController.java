package com.example.Simple.Forum.System;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    // GET请求示例：前端访问 /api/hello
    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Java后端！");
        response.put("status", "success");
        return response; // Spring Boot会自动把这个Map变成JSON格式返回
    }

    // POST请求示例：前端提交数据
    @PostMapping("/user")
    public Map<String, String> createUser(@RequestBody Map<String, Object> userData) {
        // @RequestBody 表示把前端传来的JSON数据转成Map对象
        System.out.println("收到前端数据：" + userData);

        Map<String, String> response = new HashMap<>();
        response.put("result", "用户创建成功，用户名：" + userData.get("name"));
        return response;
    }
    // ⭐ 新增：POST请求测试接口
    @PostMapping("/post")
    public Map<String, String> handlePost(@RequestBody Map<String, Object> requestData) {
        System.out.println("收到POST请求，数据：" + requestData);

        String name = (String) requestData.get("name");

        Map<String, String> response = new HashMap<>();
        response.put("message", "收到你的名字啦：" + name);
        response.put("status", "success");
        return response;
    }
}