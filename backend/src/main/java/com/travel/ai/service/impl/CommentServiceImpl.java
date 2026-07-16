package com.travel.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.ai.entity.ScenicComment;
import com.travel.ai.mapper.ScenicCommentMapper;
import com.travel.ai.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<ScenicCommentMapper, ScenicComment> implements CommentService {

    private List<String> likedComments = new ArrayList<>();

    @Override
    public List<ScenicComment> listByScenicId(Long scenicId) {
        LambdaQueryWrapper<ScenicComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicComment::getScenicId, scenicId);
        wrapper.orderByDesc(ScenicComment::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ScenicComment addComment(Long userId, Long scenicId, String content) {
        ScenicComment comment = new ScenicComment();
        comment.setUserId(userId);
        comment.setScenicId(scenicId);
        comment.setContent(content);
        comment.setLikeNum(0);
        baseMapper.insert(comment);
        return comment;
    }

    @Override
    public void likeComment(Long commentId, Long userId) {
        String key = "like:comment:" + commentId + ":" + userId;
        if (!likedComments.contains(key)) {
            likedComments.add(key);
            ScenicComment comment = baseMapper.selectById(commentId);
            if (comment != null) {
                comment.setLikeNum(comment.getLikeNum() + 1);
                baseMapper.updateById(comment);
            }
        }
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        ScenicComment comment = baseMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        baseMapper.deleteById(commentId);
    }
}