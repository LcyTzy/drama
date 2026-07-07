package com.zhenyu.drama.service.impl;

import com.zhenyu.drama.mapper.EpisodeMapper;
import com.zhenyu.drama.mapper.WatchHistoryMapper;
import com.zhenyu.drama.service.PlayService;
import com.zhenyu.pojo.dto.ProgressDTO;
import com.zhenyu.pojo.entity.Episode;
import com.zhenyu.pojo.entity.WatchHistory;
import com.zhenyu.pojo.vo.HistoryVO;
import com.zhenyu.pojo.vo.PlayUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PlayServiceImpl implements PlayService {

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private WatchHistoryMapper watchHistoryMapper;

    @Override
    public PlayUrlVO getPlayUrl(Long episodeId, Long userId) {
        Episode episode = episodeMapper.getById(episodeId);
        if (episode == null) {
            throw new RuntimeException("剧集不存在");
        }

        String expireAt = LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return PlayUrlVO.builder()
                .videoUrl(episode.getVideoUrl())
                .expireAt(expireAt)
                .duration(episode.getDuration())
                .build();
    }

    @Override
    public void syncProgress(Long userId, ProgressDTO progressDTO) {
        Episode episode = episodeMapper.getById(progressDTO.getEpisodeId());
        if (episode == null) {
            throw new RuntimeException("剧集不存在");
        }

        Integer isFinished = (progressDTO.getCurrentSeconds() >= episode.getDuration() - 5) ? 1 : 0;

        WatchHistory watchHistory = WatchHistory.builder()
                .userId(userId)
                .episodeId(progressDTO.getEpisodeId())
                .progressSeconds(progressDTO.getCurrentSeconds())
                .duration(episode.getDuration())
                .isFinished(isFinished)
                .updateTime(LocalDateTime.now())
                .build();

        watchHistoryMapper.upsert(watchHistory);
    }

    @Override
    public List<HistoryVO> getHistory(Long userId, Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        return watchHistoryMapper.listByUserId(userId, offset, pageSize);
    }
}
