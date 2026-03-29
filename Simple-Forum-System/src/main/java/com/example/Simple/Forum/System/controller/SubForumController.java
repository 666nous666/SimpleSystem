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

    /**
     * 创建子版块
     * 处理前端发送的创建子版块请求，验证参数合法性后调用服务层创建子版块
     *
     * @param request 请求体参数，包含以下字段：
     *                - name: 子版块名称（必填）
     *                - description: 子版块描述（可选，默认为空字符串）
     *                - parentForum: 父版块名称（必填）
     *                - creator: 创建者用户名（必填）
     * @return Map<String, Object> 响应结果，包含以下字段：
     *         - success: 操作是否成功，true 表示成功，false 表示失败
     *         - message: 操作结果消息提示
     *         - subForum: 创建成功的子版块对象（仅在 success=true 时返回）
     */
    @PostMapping("/create")
    public Map<String, Object> createSubForum(@RequestBody Map<String, String> request) {
        // 从请求体中提取子版块基本信息
        String name = request.get("name");
        String description = request.getOrDefault("description", "");
        String parentForum = request.get("parentForum");
        String creator = request.get("creator");

        // 验证子版块名称不能为空
        if (name == null || name.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "子版块名称不能为空"
            );
        }

        // 验证父版块不能为空
        if (parentForum == null || parentForum.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "父版块不能为空"
            );
        }

        // 验证创建者不能为空
        if (creator == null || creator.trim().isEmpty()) {
            return Map.of(
                "success", false,
                "message", "创建者不能为空"
            );
        }

        // 调用服务层创建子版块
        SubForum subForum = subForumService.createSubForum(name, description, parentForum, creator);
        
        // 根据创建结果返回相应响应
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
