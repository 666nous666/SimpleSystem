package com.example.Simple.Forum.System.service;

import com.example.Simple.Forum.System.model.SubForum;
import com.example.Simple.Forum.System.repository.SubForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubForumService {

    @Autowired
    private SubForumRepository subForumRepository;

    // 获取所有子版块列表
    public List<SubForum> getAllSubForums() {
        return subForumRepository.findAll();
    }

    // 根据 ID 获取子版块详情
    public Optional<SubForum> getSubForumById(String id) {
        return subForumRepository.findById(id);
    }

    // 根据父版块获取子版块列表
    public List<SubForum> getSubForumsByParent(String parentForum) {
        return subForumRepository.findByParentForum(parentForum);
    }

    // 根据创建者获取子版块列表
    public List<SubForum> getSubForumsByCreator(String creator) {
        return subForumRepository.findByCreator(creator);
    }

    // 创建子版块：验证参数并保存
    public SubForum createSubForum(String name, String description, String parentForum, String creator) {
        // 验证子版块名称不能为空
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        // 创建子版块对象并设置基本信息
        SubForum subForum = new SubForum();
        subForum.setName(name.trim());
        subForum.setDescription(description != null ? description.trim() : "");
        subForum.setParentForum(parentForum);
        subForum.setCreator(creator);
        subForum.setPostCount(0);
        
        // 调用数据仓库层保存子版块
        return subForumRepository.save(subForum);
    }
    // 更新子版块信息
    public SubForum updateSubForum(String id, String name, String description) {
        Optional<SubForum> optionalSubForum = subForumRepository.findById(id);
        if (optionalSubForum.isPresent()) {
            SubForum subForum = optionalSubForum.get();
            if (name != null && !name.trim().isEmpty()) {
                subForum.setName(name.trim());
            }
            if (description != null) {
                subForum.setDescription(description.trim());
            }
            return subForumRepository.save(subForum);
        }
        return null;
    }
    // 删除指定子版块
    public boolean deleteSubForum(String id) {
        if (subForumRepository.findById(id).isPresent()) {
            subForumRepository.delete(id);
            return true;
        }
        return false;
    }

    // 增加子版块的帖子数量
    public void incrementPostCount(String id) {
        subForumRepository.incrementPostCount(id);
    }
}
