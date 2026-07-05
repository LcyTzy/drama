package com.zhenyu.drama.service;

import com.zhenyu.pojo.dto.EpisodeDTO;
import com.zhenyu.pojo.entity.Episode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EpisodeService {

    void save(EpisodeDTO episodeDTO, MultipartFile videoFile);

    void update(EpisodeDTO episodeDTO);

    List<Episode> listByDramaId(Long dramaId);

    void deleteById(Long id);
}
