package com.zhenyu.drama.service;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.pojo.dto.DramaDTO;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Drama;

public interface DramaService {
    void save(DramaDTO dramaDTO);

    void update(DramaDTO dramaDTO);

    Drama getById(Long id);

    PageResult pageQuery(DramaPageQueryDTO dramaPageQueryDTO);

    void startOrStop(Integer status, Long id);

    void deleteById(Long id);
}
