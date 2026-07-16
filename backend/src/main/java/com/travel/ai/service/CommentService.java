package com.travel.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.ai.entity.ScenicComment;

import java.util.List;

public interface CommentService extends IService<ScenicComment> {
    
    List<ScenicComment> listByScenicId(Long scenicId);
    
    ScenicComment addComment(Long userId, Long scenicId, String content);
    
    void likeComment(Long commentId, Long userId);
    
    void deleteComment(Long commentId, Long userId);
}