package com.example.Simple.Forum.System.model;

// 帖子实体类，存储论坛帖子的所有信息
public class Post {
    private String id;          // 帖子唯一标识
    private String title;       // 帖子标题
    private String content;     // 帖子内容
    private String author;      // 作者用户名
    private String forum;       // 所属版块
    private String tags;        // 标签
    private String createTime;  // 创建时间
    private int votes;          // 点赞数
    private int comments;       // 评论数
    private String status;      // 审核状态：pending-待审核，approved-已通过，rejected-已驳回
    private String rejectReason; // 驳回理由
    
    public Post() {}
    
    // 构造函数：初始化帖子基本信息
    public Post(String id, String title, String content, String author, String forum, 
                String tags, String createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.forum = forum;
        this.tags = tags;
        this.createTime = createTime;
        this.votes = 0;
        this.comments = 0;
        this.status = "pending";  // 新帖子默认为待审核状态
        this.rejectReason = "";
    }
    
    // Getter 和 Setter 方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getForum() { return forum; }
    public void setForum(String forum) { this.forum = forum; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
