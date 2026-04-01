package com.example.Simple.Forum.System.service;

import com.example.Simple.Forum.System.model.Post;
import com.example.Simple.Forum.System.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 帖子服务实现类：处理帖子的业务逻辑
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    // 获取所有帖子列表
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 根据 ID 获取帖子详情
    @Override
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    // 根据版块获取帖子列表
    @Override
    public List<Post> getPostsByForum(String forum) {
        return postRepository.findByForum(forum);
    }

    // 根据作者获取帖子列表
    @Override
    public List<Post> getPostsByAuthor(String author) {
        return postRepository.findByAuthor(author);
    }

    // 创建新帖子并设置待审核状态
    @Override
    public Post createPost(String title, String content, String author, String forum, String tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setForum(forum);
        post.setTags(tags);
        post.setStatus("pending");  // 新帖子默认为待审核状态
        return postRepository.save(post);
    }

    // 更新指定帖子的信息
    @Override
    public Post updatePost(String id, String title, String content, String forum, String tags) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(title);
            post.setContent(content);
            post.setForum(forum);
            post.setTags(tags);
            return postRepository.save(post);
        }
        return null;
    }

    // 删除指定帖子
    @Override
    public void deletePost(String id) {
        postRepository.delete(id);
    }
    
    // 获取所有待审核的帖子列表
    @Override
    public List<Post> getPendingPosts() {
        return postRepository.findPendingPosts();
    }
    
    // 通过帖子审核，将状态改为 approved
    @Override
    public Post approvePost(String id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setStatus("approved");
            post.setRejectReason("");
            return postRepository.save(post);
        }
        return null;
    }
    
    // 驳回帖子审核并设置理由
    @Override
    public Post rejectPost(String id, String reason) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setStatus("rejected");
            post.setRejectReason(reason != null ? reason : "");
            return postRepository.save(post);
        }
        return null;
    }

    // 获取所有被举报的帖子列表
    @Override
    public List<Post> getReportedPosts() {
        return postRepository.findAll().stream()
                .filter(p -> "reported".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    // 举报指定帖子，将状态改为 reported
    @Override
    public Post reportPost(String id, String reason) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if ("reported".equals(post.getStatus())) {
                return null; // 已经被举报了
            }
            post.setStatus("reported");
            post.setReportReason(reason);
            return postRepository.save(post);
        }
        return null;
    }

    // 处理用户举报：确认违规或恢复帖子
    @Override
    public Post handleReport(String id, boolean confirmed) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!"reported".equals(post.getStatus())) {
                return null; // 不是被举报状态
            }
            if (confirmed) {
                post.setStatus("violated"); // 标记为违规
            } else {
                post.setStatus("approved"); // 恢复为已通过
                post.setReportReason(""); // 清除举报理由
            }
            return postRepository.save(post);
        }
        return null;
    }
    
    // 切换点赞状态：点赞或取消点赞
    @Override
    public Post toggleLike(String id, String username) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            java.util.List<String> likedUsers = post.getLikedUsers();
            
            if (likedUsers.contains(username)) {
                // 取消点赞
                likedUsers.remove(username);
                post.setVotes(likedUsers.size());
            } else {
                // 添加点赞
                likedUsers.add(username);
                post.setVotes(likedUsers.size());
            }
            return postRepository.save(post);
        }
        return null;
    }
    
    // 为帖子添加评论
    @Override
    public Post addComment(String postId, String author, String content) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            
            String commentId = String.valueOf(System.currentTimeMillis());
            String createTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            Post.Comment comment = new Post.Comment(commentId, postId, author, content, createTime);
            post.getComments().add(comment);
            post.setComments(post.getComments()); // 触发保存
            
            return postRepository.save(post);
        }
        return null;
    }

    // 获取已处理过的举报帖子列表
    @Override
    public List<Post> getHandledReports() {
        return postRepository.findAll().stream()
                .filter(p -> "violated".equals(p.getStatus()) || 
                            ("approved".equals(p.getStatus()) && p.getReportReason() != null && !p.getReportReason().isEmpty()))
                .collect(Collectors.toList());
    }
}
