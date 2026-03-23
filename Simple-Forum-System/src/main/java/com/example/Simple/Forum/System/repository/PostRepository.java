package com.example.Simple.Forum.System.repository;

import com.example.Simple.Forum.System.model.Post;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// 帖子数据仓库：负责帖子数据的持久化存储（文件存储）
@Repository
public class PostRepository {

    // 线程安全的内存数据库
    private final ConcurrentHashMap<String, Post> postDatabase = new ConcurrentHashMap<>();
    private final String dataFilePath = "data/posts.txt";
    private final AtomicLong idGenerator = new AtomicLong(1);  // 自动生成帖子 ID

    // 构造方法：初始化数据目录、加载数据、创建测试帖子
    public PostRepository() {
        ensureDataDirectoryExists();
        loadFromFile();
        initTestPosts();
    }

    // 确保数据目录存在
    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // 初始化测试帖子
    private void initTestPosts() {
        if (postDatabase.isEmpty()) {
            createTestPost("分享 Java 学习路径", "从基础到高级：1.Java 基础 2.面向对象 3.集合框架 4.IO 流 5.多线程", "张三", "技术讨论", "Java,学习");
            createTestPost("周末爬山风景分享", "今天去爬了附近的山，风景很美，空气很好！附上了几张照片。", "李四", "生活分享", "旅行，户外");
            createTestPost("新电影《星辰大海》观后感", "剧情很感人，演员演技在线，推荐大家去看！", "王五", "娱乐八卦", "电影，影评");
            saveToFile();
        }
    }

    // 创建测试帖子
    private void createTestPost(String title, String content, String author, String forum, String tags) {
        String id = String.valueOf(idGenerator.getAndIncrement());
        Post post = new Post(id, title, content, author, forum, tags, getCurrentTime());
        postDatabase.put(id, post);
    }

    // 获取当前时间（格式化字符串）
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 查询所有帖子（按时间倒序）
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>(postDatabase.values());
        posts.sort((p1, p2) -> p2.getCreateTime().compareTo(p1.getCreateTime()));
        return posts;
    }

    // 根据 ID 查询帖子
    public Optional<Post> findById(String id) {
        return Optional.ofNullable(postDatabase.get(id));
    }

    // 根据版块查询帖子
    public List<Post> findByForum(String forum) {
        List<Post> posts = new ArrayList<>();
        for (Post post : postDatabase.values()) {
            if (post.getForum().equals(forum)) {
                posts.add(post);
            }
        }
        return posts;
    }

    // 根据作者查询帖子
    public List<Post> findByAuthor(String author) {
        List<Post> posts = new ArrayList<>();
        for (Post post : postDatabase.values()) {
            if (post.getAuthor().equals(author)) {
                posts.add(post);
            }
        }
        return posts;
    }

    // 查询待审核帖子
    public List<Post> findPendingPosts() {
        List<Post> posts = new ArrayList<>();
        for (Post post : postDatabase.values()) {
            if ("pending".equals(post.getStatus())) {
                posts.add(post);
            }
        }
        return posts;
    }

    // 保存帖子（新增或更新）
    public Post save(Post post) {
        if (post.getId() == null || post.getId().isEmpty()) {
            String id = String.valueOf(idGenerator.getAndIncrement());
            post.setId(id);
            post.setCreateTime(getCurrentTime());
        }
        postDatabase.put(post.getId(), post);
        saveToFile();
        return post;
    }

    // 删除帖子
    public void delete(String id) {
        postDatabase.remove(id);
        saveToFile();
    }

    // 从文件加载帖子数据
    private void loadFromFile() {
        File file = new File(dataFilePath);
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    String[] parts = line.split("\\|", 10);
                    if (parts.length >= 8) {
                        String id = parts[0];
                        String title = parts[1];
                        String content = parts[2];
                        String author = parts[3];
                        String forum = parts[4];
                        String tags = parts[5];
                        String time = parts[6];
                        int votes = Integer.parseInt(parts[7]);
                        
                        Post post = new Post(id, title, content, author, forum, tags, time);
                        post.setVotes(votes);
                        
                        // 加载审核状态和驳回理由（兼容旧数据）
                        if (parts.length >= 9) {
                            post.setStatus(parts[8]);
                        } else {
                            post.setStatus("pending");  // 旧数据也设为待审核状态
                        }
                        
                        if (parts.length >= 10) {
                            post.setRejectReason(parts[9]);
                        }
                        
                        postDatabase.put(id, post);
                        
                        long currentId = Long.parseLong(id);
                        if (currentId >= idGenerator.get()) {
                            idGenerator.set(currentId + 1);
                        }
                    }
                }
                System.out.println("从文件加载了 " + postDatabase.size() + " 个帖子");
            } catch (IOException e) {
                System.err.println("加载帖子数据失败：" + e.getMessage());
            }
        }
    }

    // 保存帖子数据到文件
    private void saveToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (Post post : postDatabase.values()) {
                String line = String.format("%s|%s|%s|%s|%s|%s|%s|%d|%s|%s|%s",
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor(),
                        post.getForum(),
                        post.getTags(),
                        post.getCreateTime(),
                        post.getVotes(),
                        post.getStatus() != null ? post.getStatus() : "pending",
                        post.getRejectReason() != null ? post.getRejectReason() : "",
                        post.getReportReason() != null ? post.getReportReason() : ""
                );
                lines.add(line);
            }
            Files.write(Paths.get(dataFilePath), lines);
            System.out.println("帖子数据已保存到文件");
        } catch (IOException e) {
            System.err.println("保存帖子数据失败：" + e.getMessage());
        }
    }
}
