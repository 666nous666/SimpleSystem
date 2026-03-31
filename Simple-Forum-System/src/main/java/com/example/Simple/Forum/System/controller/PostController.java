package com.example.Simple.Forum.System.controller;

import com.example.Simple.Forum.System.model.Post;
import com.example.Simple.Forum.System.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 帖子控制器：处理帖子的增删改查请求
@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    // 获取所有帖子
    @GetMapping("/all")
    public Map<String, Object> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return Map.of(
            "success", true,
            "posts", posts
        );
    }

    // 根据 ID 获取帖子详情
    @GetMapping("/{id}")
    public Map<String, Object> getPostById(@PathVariable String id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            return Map.of(
                "success", true,
                "post", post.get()
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在"
            );
        }
    }

    // 根据版块获取帖子
    @GetMapping("/forum/{forum}")
    public Map<String, Object> getPostsByForum(@PathVariable String forum) {
        List<Post> posts = postService.getPostsByForum(forum);
        return Map.of(
            "success", true,
            "posts", posts
        );
    }

    // 根据作者获取帖子
    @GetMapping("/author/{author}")
    public Map<String, Object> getPostsByAuthor(@PathVariable String author) {
        List<Post> posts = postService.getPostsByAuthor(author);
        return Map.of(
            "success", true,
            "posts", posts
        );
    }

    // 创建新帖子
    @PostMapping("/create")
    public Map<String, Object> createPost(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");
        String author = request.get("author");
        String forum = request.get("forum");
        String tags = request.getOrDefault("tags", "");

        if (title == null || title.isEmpty()) {
            return Map.of(
                "success", false,
                "message", "标题不能为空"
            );
        }

        Post post = postService.createPost(title, content, author, forum, tags);
        return Map.of(
            "success", true,
            "message", "发帖成功",
            "post", post
        );
    }

    // 更新帖子
    @PutMapping("/update/{id}")
    public Map<String, Object> updatePost(@PathVariable String id, 
                                          @RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");
        String forum = request.get("forum");
        String tags = request.getOrDefault("tags", "");

        Post post = postService.updatePost(id, title, content, forum, tags);
        if (post != null) {
            return Map.of(
                "success", true,
                "message", "更新成功",
                "post", post
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在"
            );
        }
    }

    // 删除帖子
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return Map.of(
            "success", true,
            "message", "删除成功"
        );
    }
    
    // 获取待审核帖子列表
    @GetMapping("/pending")
    public Map<String, Object> getPendingPosts() {
        List<Post> posts = postService.getPendingPosts();
        return Map.of(
            "success", true,
            "posts", posts
        );
    }
    
    // 获取已通过帖子列表
    @GetMapping("/approved")
    public Map<String, Object> getApprovedPosts() {
        List<Post> posts = postService.getAllPosts().stream()
                .filter(p -> "approved".equals(p.getStatus()))
                .toList();
        return Map.of(
            "success", true,
            "posts", posts
        );
    }
    
    // 通过帖子审核
    @PutMapping("/approve/{id}")
    public Map<String, Object> approvePost(@PathVariable String id) {
        Post post = postService.approvePost(id);
        if (post != null) {
            return Map.of(
                "success", true,
                "message", "审核通过",
                "post", post
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在"
            );
        }
    }
    
    // 驳回帖子审核
    @PutMapping("/reject/{id}")
    public Map<String, Object> rejectPost(@PathVariable String id,
                                          @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("rejectReason", "");
        Post post = postService.rejectPost(id, reason);
        if (post != null) {
            return Map.of(
                "success", true,
                "message", "已驳回",
                "post", post
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在"
            );
        }
    }
    
    // 获取被举报的帖子列表
    @GetMapping("/reported")
    public Map<String, Object> getReportedPosts() {
        List<Post> posts = postService.getReportedPosts();
        return Map.of(
            "success", true,
            "posts", posts
        );
    }

    // 举报帖子
    @PutMapping("/report/{id}")
    public Map<String, Object> reportPost(@PathVariable String id,
                                      @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reportReason", "");
        if (reason.isEmpty()) {
            return Map.of(
                "success", false,
                "message", "举报理由不能为空"
            );
        }
        Post post = postService.reportPost(id, reason);
        if (post != null) {
            return Map.of(
                "success", true,
                "message", "举报成功"
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在或已被举报"
            );
        }
    }

    // 处理举报
    @PutMapping("/handle-report/{id}")
    public Map<String, Object> handleReport(@PathVariable String id,
                                        @RequestBody Map<String, Boolean> request) {
        boolean confirmed = request.getOrDefault("confirmed", false);
        Post post = postService.handleReport(id, confirmed);
        if (post != null) {
            return Map.of(
                "success", true,
                "message", confirmed ? "已标记为违规" : "举报已驳回"
            );
        } else {
            return Map.of(
                "success", false,
                "message", "帖子不存在或状态异常"
            );
        }
    }

    // 获取已处理的举报列表
    @GetMapping("/handled-reports")
    public Map<String, Object> getHandledReports() {
        List<Post> posts = postService.getHandledReports();
        return Map.of(
            "success", true,
            "posts", posts
        );
    }
}
