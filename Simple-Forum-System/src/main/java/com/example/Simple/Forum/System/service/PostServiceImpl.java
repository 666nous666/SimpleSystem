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

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getPostsByForum(String forum) {
        return postRepository.findByForum(forum);
    }

    @Override
    public List<Post> getPostsByAuthor(String author) {
        return postRepository.findByAuthor(author);
    }

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

    @Override
    public void deletePost(String id) {
        postRepository.delete(id);
    }
    
    @Override
    public List<Post> getPendingPosts() {
        return postRepository.findPendingPosts();
    }
    
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

    @Override
    public List<Post> getReportedPosts() {
        return postRepository.findAll().stream()
                .filter(p -> "reported".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

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
}
