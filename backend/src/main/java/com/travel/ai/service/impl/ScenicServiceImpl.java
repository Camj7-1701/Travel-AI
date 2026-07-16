package com.travel.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.ai.entity.Scenic;
import com.travel.ai.mapper.ScenicMapper;
import com.travel.ai.service.ScenicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScenicServiceImpl extends ServiceImpl<ScenicMapper, Scenic> implements ScenicService {

    @Override
    public List<Scenic> listByCategory(String category) {
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Scenic::getCategory, category);
        }
        wrapper.orderByDesc(Scenic::getHot);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Scenic> listByCategoryAndKeyword(String category, String keyword) {
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Scenic::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Scenic::getScenicName, keyword).or()
                   .like(Scenic::getAddress, keyword);
        }
        wrapper.orderByDesc(Scenic::getHot);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Scenic> listHot(Integer limit) {
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Scenic::getHot);
        Page<Scenic> page = new Page<>(1, limit);
        return baseMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public Scenic getDetail(Long id) {
        return baseMapper.selectById(id);
    }
}