package com.zhenyu.drama.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhenyu.common.constant.MessageConstant;
import com.zhenyu.common.constant.StatusConstant;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.drama.mapper.CategoryMapper;
import com.zhenyu.drama.mapper.DramaMapper;
import com.zhenyu.drama.service.CategoryService;
import com.zhenyu.pojo.dto.CategoryDTO;
import com.zhenyu.pojo.dto.CategoryPageQueryDTO;
import com.zhenyu.pojo.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DramaMapper dramaMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        // 属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        // 分类状态默认为禁用
        category.setStatus(StatusConstant.DISABLE);

        categoryMapper.insert(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        // 下一条sql进行分页，自动加入limit关键字分页
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(Long id) {
        // 查询当前分类是否关联了 drama
        Integer count = dramaMapper.countByCategoryId(id);
        if (count > 0) {
            // 当前分类下有 drama， 不允许删除
            throw new RuntimeException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        // 删除分类
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void batchStatus(Integer status, List<Long> ids) {
        for (Long id : ids) {
            Category category = Category.builder()
                    .id(id)
                    .status(status)
                    .build();
            categoryMapper.update(category);
        }

    }
}
