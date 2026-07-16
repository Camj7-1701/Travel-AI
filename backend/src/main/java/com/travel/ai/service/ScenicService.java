package com.travel.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.ai.entity.Scenic;

import java.util.List;

public interface ScenicService extends IService<Scenic> {
    
    List<Scenic> listByCategory(String category);
    
    List<Scenic> listByCategoryAndKeyword(String category, String keyword);
    
    List<Scenic> listHot(Integer limit);
    
    Scenic getDetail(Long id);
}