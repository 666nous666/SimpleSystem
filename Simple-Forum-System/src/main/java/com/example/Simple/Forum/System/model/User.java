package com.example.Simple.Forum.System.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用户基础类，包含所有用户的公共属性和方法
@NoArgsConstructor
@Data
@AllArgsConstructor
public class User {
    private String username;      // 用户名
    private String passwordHash;  // 密码哈希值
    private String salt;          // 盐值
    private int level;            // 权限等级

    // 基础功能：所有用户都有
    public boolean canPost() { return true; }           // 可以发帖
    public boolean canComment() { return true; }        // 可以评论
    public boolean canView() { return true; }           // 可以浏览

}