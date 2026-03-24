package com.example.Simple.Forum.System.service;

import com.example.Simple.Forum.System.model.Post;
import java.util.List;
import java.util.Optional;

// 帖子服务接口：定义帖子相关的业务方法
public interface PostService {
    List<Post> getAllPosts();
    Optional<Post> getPostById(String id);
    List<Post> getPostsByForum(String forum);
    List<Post> getPostsByAuthor(String author);
    Post createPost(String title, String content, String author, String forum, String tags);
    Post updatePost(String id, String title, String content, String forum, String tags);
    void deletePost(String id);

    // 审核相关方法
    List<Post> getPendingPosts();
    Post approvePost(String id);
    Post rejectPost(String id, String reason);

    // 举报相关方法
    List<Post> getReportedPosts();
    Post reportPost(String id, String reason);
    Post handleReport(String id, boolean confirmed);
    List<Post> getHandledReports();

}
