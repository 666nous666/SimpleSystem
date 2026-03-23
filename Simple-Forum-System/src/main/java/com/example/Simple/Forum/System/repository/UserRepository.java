package com.example.Simple.Forum.System.repository;

import com.example.Simple.Forum.System.model.*;
import com.example.Simple.Forum.System.util.PasswordUtil;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// 用户数据仓库：负责用户数据的持久化存储（文件存储）
@Repository
public class UserRepository {

    // 线程安全的内存数据库
    private final ConcurrentHashMap<String, User> userDatabase = new ConcurrentHashMap<>();
    private final String dataFilePath = "data/users.txt";

    // 构造方法：初始化数据目录、加载数据、创建测试用户
    public UserRepository() {
        ensureDataDirectoryExists();
        loadFromFile();
        initTestUsers();
    }

    // 确保数据目录存在
    private void ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // 初始化测试用户（管理员、版主、普通用户）
    private void initTestUsers() {
        if (userDatabase.isEmpty()) {
            createTestUser("admin", "admin123", 0);
            createTestUser("moderator", "mod123", 1);
            createTestUser("user1", "user123", 2);
            createTestUser("user2", "user123", 2);
            saveToFile();
            System.out.println("测试用户创建完成！");
        }
    }

    // 创建测试用户（加密密码）
    private void createTestUser(String username, String password, int level) {
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        User user;
        switch (level) {
            case 0:
                user = new Admin(username, passwordHash, salt);
                break;
            case 1:
                user = new Moderator(username, passwordHash, salt);
                break;
            default:
                user = new OrdinaryUser(username, passwordHash, salt);
                break;
        }
        userDatabase.put(username, user);
    }

    // 根据用户名查询用户
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userDatabase.get(username));
    }

    // 保存用户（如果已存在则返回 false）
    public boolean save(User user) {
        if (userDatabase.containsKey(user.getUsername())) {
            return false;
        }
        userDatabase.put(user.getUsername(), user);
        saveToFile();
        return true;
    }

    // 从文件加载用户数据
    private void loadFromFile() {
        File file = new File(dataFilePath);
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String username = parts[0];
                        String passwordHash = parts[1];
                        String salt = parts[2];
                        int level = Integer.parseInt(parts[3]);

                        User user;
                        switch (level) {
                            case 0:
                                user = new Admin(username, passwordHash, salt);
                                break;
                            case 1:
                                user = new Moderator(username, passwordHash, salt);
                                break;
                            default:
                                user = new OrdinaryUser(username, passwordHash, salt);
                                break;
                        }
                        userDatabase.put(username, user);
                    }
                }
                System.out.println("从文件加载了 " + userDatabase.size() + " 个用户");
            } catch (IOException e) {
                System.err.println("加载用户数据失败：" + e.getMessage());
            }
        }
    }

    // 保存用户数据到文件
    private void saveToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (User user : userDatabase.values()) {
                String line = String.format("%s,%s,%s,%d",
                        user.getUsername(),
                        user.getPasswordHash(),
                        user.getSalt(),
                        user.getLevel()
                );
                lines.add(line);
            }
            Files.write(Paths.get(dataFilePath), lines);
            System.out.println("用户数据已保存到文件");
        } catch (IOException e) {
            System.err.println("保存用户数据失败：" + e.getMessage());
        }
    }
}