package com.zhenyu.drama.service;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.pojo.dto.DramaDTO;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Drama;
import com.zhenyu.pojo.vo.DramaDetailVO;
import com.zhenyu.pojo.vo.DramaRecommendVO;

import java.util.List;

public interface DramaService {
    void save(DramaDTO dramaDTO);

    void update(DramaDTO dramaDTO);

    Drama getById(Long id);

    PageResult pageQuery(DramaPageQueryDTO dramaPageQueryDTO);

    void startOrStop(Integer status, Long id);

    void deleteById(Long id);

    List<DramaRecommendVO> recommend(Long categoryId, Integer page, Integer pageSize);

    DramaDetailVO getDetail(Long id, Long userId);
}
