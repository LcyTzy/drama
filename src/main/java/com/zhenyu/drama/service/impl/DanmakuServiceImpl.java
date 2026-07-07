package com.zhenyu.drama.service.impl;

import com.zhenyu.drama.mapper.DanmakuMapper;
import com.zhenyu.drama.service.DanmakuService;
import com.zhenyu.pojo.dto.DanmakuDTO;
import com.zhenyu.pojo.entity.Danmaku;
import com.zhenyu.pojo.vo.DanmakuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DanmakuServiceImpl implements DanmakuService {

    @Autowired
    private DanmakuMapper danmakuMapper;

    @Override
    public List<DanmakuVO> list(Long episodeId, Integer startTime, Integer endTime) {
        return danmakuMapper.listByEpisodeId(episodeId, startTime, endTime);
    }

    @Override
    public void send(Long userId, DanmakuDTO danmakuDTO) {
        Danmaku danmaku = Danmaku.builder()
                .userId(userId)
                .episodeId(danmakuDTO.getEpisodeId())
                .content(danmakuDTO.getContent())
                .timestamp(danmakuDTO.getTimestamp())
                .color(danmakuDTO.getColor() != null ? danmakuDTO.getColor() : "#FFFFFF")
                .createTime(LocalDateTime.now())
                .build();
        danmakuMapper.insert(danmaku);
    }

    @Override
    public void delete(Long userId, Long id) {
        danmakuMapper.deleteByIdAndUserId(id, userId);
    }
}
