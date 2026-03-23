package com.example.Simple.Forum.System.model;

// 管理员类，继承自 User，拥有最高权限
public class Admin extends User {

    // 构造方法：设置管理员等级为 0
    public Admin(String username, String passwordHash, String salt) {
        super(username, passwordHash, salt, 0);  // 管理员等级为 0
    }

    // 管理员的额外功能
    public boolean canManageUsers() { return true; }     // 可以管理用户
    public boolean canManageForums() { return true; }    // 可以管理论坛
    public boolean canViewLogs() { return true; }        // 可以查看日志
    public boolean canAssignModerator() { return true; } // 可以指派版主
}