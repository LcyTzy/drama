package com.zhenyu.drama.service;

import com.zhenyu.pojo.dto.ProgressDTO;
import com.zhenyu.pojo.vo.HistoryVO;
import com.zhenyu.pojo.vo.PlayUrlVO;

import java.util.List;

public interface PlayService {
    PlayUrlVO getPlayUrl(Long episodeId, Long userId);

    void syncProgress(Long userId, ProgressDTO progressDTO);

    List<HistoryVO> getHistory(Long userId, Integer page, Integer pageSize);
}
