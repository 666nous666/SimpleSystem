package com.example.Simple.Forum.System.controller;

import com.example.Simple.Forum.System.model.SubForum;
import com.example.Simple.Forum.System.service.SubForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subforum")
@CrossOrigin(origins = "*")
public class SubForumController {

    @Autowired
    private SubForumService subForumService;

    @GetMapping("/all")
    public Map<String, Object> getAllSubForums() {
        List<SubForum> subForums = subForumService.getAllSubForums();
        return Map.of(
            "success", true,
            "subForums", subForums
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getSubForumById(@PathVariable String id) {
        Optional<SubForum> subForum = subForumService.getSubForumById(id);
        if (subForum.isPresent()) {
            return Map.of(
                "success", true,
                "subForum", subForum.get()
            );
        } else {
            return Map.of(
                "success", false,
                "message", "子版块不存在"
            );
        }
    }

    @GetMapping("/parent/{parentForum}")
    public Map<String, Object> getSubForumsByParent(@PathVariable String parentForum) {
        List<SubForum> subForums = subForumService.getSubForumsByParent(parentForum);
        return Map.of(
            "success", true,
            "subForums", subForums
        );
    }

    @PostMapping("/create")
    public Map<String, Object> createSubForum(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.getOrDefault("description", "");
        String parentForum = request.get("parentForum");
        String creator = request.get("creator");

        if (name == null || name.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "子版块名称不能为空"
            );
        }

        if (parentForum == null || parentForum.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "父版块不能为空"
            );
        }

        if (creator == null || creator.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "创建者不能为空"
            );
        }

        SubForum subForum = subForumService.createSubForum(name, description, parentForum, creator);
        if (subForum != null) {
            return Map.of(
                "success", true,
                "message", "创建成功",
                "subForum", subForum
            );
        } else {
            return Map.of(
                "success", false,
                "message", "创建失败"
            );
        }
    }

    @PutMapping("/update/{id}")
    public Map<String, Object> updateSubForum(@PathVariable String id,
                                              @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.getOrDefault("description", "");

        SubForum subForum = subForumService.updateSubForum(id, name, description);
        if (subForum != null) {
            return Map.of(
                "success", true,
                "message", "更新成功",
                "subForum", subForum
            );
        } else {
            return Map.of(
                "success", false,
                "message", "子版块不存在"
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteSubForum(@PathVariable String id) {
        boolean deleted = subForumService.deleteSubForum(id);
        if (deleted) {
            return Map.of(
                "success", true,
                "message", "删除成功"
            );
        } else {
            return Map.of(
                "success", false,
                "message", "子版块不存在"
            );
        }
    }
}
