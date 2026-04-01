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

    public List<SubForum> getAllSubForums() {
        return subForumRepository.findAll();
    }

    public Optional<SubForum> getSubForumById(String id) {
        return subForumRepository.findById(id);
    }

    public List<SubForum> getSubForumsByParent(String parentForum) {
        return subForumRepository.findByParentForum(parentForum);
    }

    public List<SubForum> getSubForumsByCreator(String creator) {
        return subForumRepository.findByCreator(creator);
    }

    //创建子版块
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
    //更新子版块
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
    //删除子版块
    public boolean deleteSubForum(String id) {
        if (subForumRepository.findById(id).isPresent()) {
            subForumRepository.delete(id);
            return true;
        }
        return false;
    }

    public void incrementPostCount(String id) {
        subForumRepository.incrementPostCount(id);
    }
}
