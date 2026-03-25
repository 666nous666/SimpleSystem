package com.example.Simple.Forum.System.model;

import lombok.Data;

// 帖子实体类，存储论坛帖子的所有信息
@Data
public class Post {
    private String id;          // 帖子唯一标识
    private String title;       // 帖子标题
    private String content;     // 帖子内容
    private String author;      // 作者用户名
    private String forum;       // 所属版块
    private String subForum;    // 所属子板块
    private String tags;        // 标签
    private String createTime;  // 创建时间
    private int votes;          // 点赞数
    private int comments;       // 评论数
    private String status;      // 审核状态：pending-待审核，approved-已通过，rejected-已驳回
    private String rejectReason; // 驳回理由
    private String reportReason; //举报理由
    
    public Post() {}
    
    // 构造函数：初始化帖子基本信息
    public Post(String id, String title, String content, String author, String forum, 
                String tags, String createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.forum = forum;
        this.subForum = "";
        this.tags = tags;
        this.createTime = createTime;
        this.votes = 0;
        this.comments = 0;
        this.status = "pending";
        this.rejectReason = "";
    }

}
