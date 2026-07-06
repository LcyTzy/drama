package com.zhenyu.drama.service.impl;

import com.zhenyu.common.utils.AliOssUtil;
import com.zhenyu.drama.mapper.DramaMapper;
import com.zhenyu.drama.mapper.EpisodeMapper;
import com.zhenyu.drama.service.EpisodeService;
import com.zhenyu.pojo.dto.EpisodeDTO;
import com.zhenyu.pojo.entity.Drama;
import com.zhenyu.pojo.entity.Episode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EpisodeServiceImpl implements EpisodeService {

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private DramaMapper dramaMapper;

    @Autowired
    private AliOssUtil aliOssUtil;

    @Override
    public void saveWithVideoUrl(EpisodeDTO episodeDTO) {
        Episode episode = new Episode();
        BeanUtils.copyProperties(episodeDTO, episode);
        episodeMapper.insert(episode);
        updateTotalEpisodes(episodeDTO.getDramaId());
    }

    @Override
    public void save(EpisodeDTO episodeDTO, MultipartFile videoFile) {
        Episode episode = new Episode();
        BeanUtils.copyProperties(episodeDTO, episode);

        // 上传视频到OSS
        try {
            String originalFilename = videoFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = "video/" + UUID.randomUUID().toString() + extension;
            String videoUrl = aliOssUtil.upload(videoFile.getBytes(), objectName);
            episode.setVideoUrl(videoUrl);
            episode.setFileSize(videoFile.getSize());
        } catch (IOException e) {
            log.error("视频上传失败：{}", e);
            throw new RuntimeException("视频上传失败");
        }

        episodeMapper.insert(episode);

        // 更新短剧的总集数
        updateTotalEpisodes(episodeDTO.getDramaId());
    }

    @Override
    public void update(EpisodeDTO episodeDTO) {
        Episode episode = new Episode();
        BeanUtils.copyProperties(episodeDTO, episode);
        episodeMapper.update(episode);
    }

    @Override
    public List<Episode> listByDramaId(Long dramaId) {
        return episodeMapper.listByDramaId(dramaId);
    }

    @Override
    public void deleteById(Long id) {
        Episode episode = episodeMapper.getById(id);
        if (episode != null) {
            episodeMapper.deleteById(id);
            // 更新短剧的总集数
            updateTotalEpisodes(episode.getDramaId());
        }
    }

    /**
     * 更新短剧的总集数
     */
    private void updateTotalEpisodes(Long dramaId) {
        Integer count = episodeMapper.countByDramaId(dramaId);
        Drama drama = Drama.builder().id(dramaId).totalEpisodes(count).build();
        dramaMapper.update(drama);
    }
}
