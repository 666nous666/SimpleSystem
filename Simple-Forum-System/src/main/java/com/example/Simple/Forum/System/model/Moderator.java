package com.example.Simple.Forum.System.model;

// 版主类，继承自 User，拥有管理帖子的权限
public class Moderator extends User {

    // 构造方法：设置版主等级为 1
    public Moderator(String username, String passwordHash, String salt) {
        super(username, passwordHash, salt, 1);  // 版主等级为 1
    }

    // 版主的额外功能
    public boolean canDeletePost() { return true; }     // 可以删除帖子
    public boolean canPinPost() { return true; }        // 可以置顶帖子
    public boolean canBanUser() { return true; }        // 可以禁言用户
}