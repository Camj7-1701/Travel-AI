package com.travel.ai.controller;

import com.travel.ai.common.Result;
import com.travel.ai.entity.ScenicComment;
import com.travel.ai.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list/{scenicId}")
    public Result<List<ScenicComment>> list(@PathVariable Long scenicId) {
        List<ScenicComment> list = commentService.listByScenicId(scenicId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<ScenicComment> add(@RequestBody Map<String, Object> params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        Long scenicId = Long.valueOf(params.get("scenicId").toString());
        String content = params.get("content").toString();
        
        try {
            ScenicComment comment = commentService.addComment(userId, scenicId, content);
            return Result.success("评论成功", comment);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/like/{commentId}")
    public Result<Map<String, Object>> like(@PathVariable Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        commentService.likeComment(commentId, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("message", "点赞成功");
        return Result.success(data);
    }

    @DeleteMapping("/delete/{commentId}")
    public Result<Map<String, Object>> delete(@PathVariable Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        try {
            commentService.deleteComment(commentId, userId);
            Map<String, Object> data = new HashMap<>();
            data.put("message", "删除成功");
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}