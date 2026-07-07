package com.zhenyu.drama.service;

import com.zhenyu.pojo.dto.DanmakuDTO;
import com.zhenyu.pojo.vo.DanmakuVO;

import java.util.List;

public interface DanmakuService {
    List<DanmakuVO> list(Long episodeId, Integer startTime, Integer endTime);

    void send(Long userId, DanmakuDTO danmakuDTO);

    void delete(Long userId, Long id);
}
