package com.example.Simple.Forum.System.model;

// 普通用户类，继承自 User，拥有基础权限
public class OrdinaryUser extends User {

    // 构造方法：设置普通用户等级为 2
    public OrdinaryUser(String username, String passwordHash, String salt) {
        super(username, passwordHash, salt, 2);  // 普通用户等级为 2
    }

    // 普通用户的特有功能
    public boolean canEditOwnPost() { return true; }  // 可以编辑自己的帖子
}