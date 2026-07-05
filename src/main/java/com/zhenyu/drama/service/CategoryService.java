package com.zhenyu.drama.service;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.pojo.dto.CategoryDTO;
import com.zhenyu.pojo.dto.CategoryPageQueryDTO;
import com.zhenyu.pojo.entity.Category;

import java.util.List;

public interface CategoryService {
    void save(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteById(Long id);

    List<Category> list(Integer type);

    void startOrStop(Integer status, Long id);
}
