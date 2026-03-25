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

    public SubForum createSubForum(String name, String description, String parentForum, String creator) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        SubForum subForum = new SubForum();
        subForum.setName(name.trim());
        subForum.setDescription(description != null ? description.trim() : "");
        subForum.setParentForum(parentForum);
        subForum.setCreator(creator);
        subForum.setPostCount(0);
        
        return subForumRepository.save(subForum);
    }

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
