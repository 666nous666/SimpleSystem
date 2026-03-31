package com.example.Simple.Forum.System.model;

import lombok.Data;

// 子版块实体类
@Data
public class SubForum {
    private String id;              // 子版块唯一标识
    private String name;            // 子版块名称
    private String description;     // 描述
    private String parentForum;     // 父版块名称
    private String creator;         // 创建者用户名
    private int postCount;          // 帖子数量
    private String createTime;      // 创建时间
    
    public SubForum() {}
    
    public SubForum(String id, String name, String description, String parentForum, 
                   String creator, int postCount, String createTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentForum = parentForum;
        this.creator = creator;
        this.postCount = postCount;
        this.createTime = createTime;
    }
}
