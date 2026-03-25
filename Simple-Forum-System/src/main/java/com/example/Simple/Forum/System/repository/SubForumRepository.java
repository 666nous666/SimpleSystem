package com.example.Simple.Forum.System.repository;

import com.example.Simple.Forum.System.model.SubForum;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// 子版块数据仓库：负责子版块数据的持久化存储
@Repository
public class SubForumRepository {

    private final ConcurrentHashMap<String, SubForum> subForumDatabase = new ConcurrentHashMap<>();
    private final String dataFilePath = "data/subforums.txt";
    private final AtomicLong idGenerator = new AtomicLong(1);

    public SubForumRepository() {
        ensureDataDirectoryExists();
        loadFromFile();
        initTestSubForums();
    }

    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initTestSubForums() {
        if (subForumDatabase.isEmpty()) {
            createTestSubForum("日常闲聊", "分享日常生活点滴", "生活分享", "管理员");
            createTestSubForum("美食天地", "交流美食制作技巧", "生活分享", "管理员");
            createTestSubForum("Java 技术", "Java 编程技术讨论", "技术讨论", "管理员");
            createTestSubForum("前端开发", "Web 前端技术交流", "技术讨论", "管理员");
            createTestSubForum("影视评论", "电影电视剧观后感", "娱乐八卦", "管理员");
            saveToFile();
        }
    }

    private void createTestSubForum(String name, String description, String parentForum, String creator) {
        String id = String.valueOf(idGenerator.getAndIncrement());
        SubForum subForum = new SubForum(id, name, description, parentForum, creator, 0, getCurrentTime());
        subForumDatabase.put(id, subForum);
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public List<SubForum> findAll() {
        List<SubForum> subForums = new ArrayList<>(subForumDatabase.values());
        subForums.sort((s1, s2) -> s2.getCreateTime().compareTo(s1.getCreateTime()));
        return subForums;
    }

    public Optional<SubForum> findById(String id) {
        return Optional.ofNullable(subForumDatabase.get(id));
    }

    public List<SubForum> findByParentForum(String parentForum) {
        List<SubForum> subForums = new ArrayList<>();
        for (SubForum subForum : subForumDatabase.values()) {
            if (subForum.getParentForum().equals(parentForum)) {
                subForums.add(subForum);
            }
        }
        return subForums;
    }

    public List<SubForum> findByCreator(String creator) {
        List<SubForum> subForums = new ArrayList<>();
        for (SubForum subForum : subForumDatabase.values()) {
            if (subForum.getCreator().equals(creator)) {
                subForums.add(subForum);
            }
        }
        return subForums;
    }

    public SubForum save(SubForum subForum) {
        if (subForum.getId() == null || subForum.getId().isEmpty()) {
            String id = String.valueOf(idGenerator.getAndIncrement());
            subForum.setId(id);
            subForum.setCreateTime(getCurrentTime());
        }
        subForumDatabase.put(subForum.getId(), subForum);
        saveToFile();
        return subForum;
    }

    public void delete(String id) {
        subForumDatabase.remove(id);
        saveToFile();
    }

    public void incrementPostCount(String id) {
        SubForum subForum = subForumDatabase.get(id);
        if (subForum != null) {
            subForum.setPostCount(subForum.getPostCount() + 1);
            saveToFile();
        }
    }

    private void loadFromFile() {
        File file = new File(dataFilePath);
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    String[] parts = line.split("\\|", 7);
                    if (parts.length >= 6) {
                        String id = parts[0];
                        String name = parts[1];
                        String description = parts[2];
                        String parentForum = parts[3];
                        String creator = parts[4];
                        int postCount = Integer.parseInt(parts[5]);
                        
                        SubForum subForum = new SubForum(id, name, description, parentForum, creator, postCount, "");
                        
                        if (parts.length >= 7) {
                            subForum.setCreateTime(parts[6]);
                        }
                        
                        subForumDatabase.put(id, subForum);
                        
                        long currentId = Long.parseLong(id);
                        if (currentId >= idGenerator.get()) {
                            idGenerator.set(currentId + 1);
                        }
                    }
                }
                System.out.println("从文件加载了 " + subForumDatabase.size() + " 个子版块");
            } catch (IOException e) {
                System.err.println("加载子版块数据失败：" + e.getMessage());
            }
        }
    }

    private void saveToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (SubForum subForum : subForumDatabase.values()) {
                String line = String.format("%s|%s|%s|%s|%s|%d|%s",
                        subForum.getId(),
                        subForum.getName(),
                        subForum.getDescription(),
                        subForum.getParentForum(),
                        subForum.getCreator(),
                        subForum.getPostCount(),
                        subForum.getCreateTime() != null ? subForum.getCreateTime() : getCurrentTime()
                );
                lines.add(line);
            }
            Files.write(Paths.get(dataFilePath), lines);
            System.out.println("子版块数据已保存到文件");
        } catch (IOException e) {
            System.err.println("保存子版块数据失败：" + e.getMessage());
        }
    }
}
