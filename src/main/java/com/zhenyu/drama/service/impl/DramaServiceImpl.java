package com.zhenyu.drama.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhenyu.common.constant.StatusConstant;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.drama.mapper.DramaMapper;
import com.zhenyu.drama.service.DramaService;
import com.zhenyu.pojo.dto.DramaDTO;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Drama;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DramaServiceImpl implements DramaService {
    @Autowired
    private DramaMapper dramaMapper;

    @Override
    public void save(DramaDTO dramaDTO) {
        Drama drama = new Drama();
        BeanUtils.copyProperties(dramaDTO, drama);
        drama.setStatus(StatusConstant.ENABLE);
        drama.setViews(0L);
        dramaMapper.insert(drama);
    }

    @Override
    public void update(DramaDTO dramaDTO) {
        Drama drama = new Drama();
        BeanUtils.copyProperties(dramaDTO, drama);
        dramaMapper.update(drama);
    }

    @Override
    public Drama getById(Long id) {
        return dramaMapper.getById(id);
    }

    @Override
    public PageResult pageQuery(DramaPageQueryDTO dramaPageQueryDTO) {
        PageHelper.startPage(dramaPageQueryDTO.getPage(), dramaPageQueryDTO.getPageSize());
        Page<Drama> page = dramaMapper.pageQuery(dramaPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Drama drama = Drama.builder().id(id).status(status).build();
        dramaMapper.update(drama);
    }

    @Override
    public void deleteById(Long id) {
        dramaMapper.deleteById(id);
    }
}
