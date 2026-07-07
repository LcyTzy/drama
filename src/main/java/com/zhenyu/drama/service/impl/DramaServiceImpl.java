package com.zhenyu.drama.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhenyu.common.constant.StatusConstant;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.drama.mapper.CategoryMapper;
import com.zhenyu.drama.mapper.DramaMapper;
import com.zhenyu.drama.mapper.EpisodeMapper;
import com.zhenyu.drama.mapper.UserUnlockMapper;
import com.zhenyu.drama.service.DramaService;
import com.zhenyu.pojo.dto.DramaDTO;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Category;
import com.zhenyu.pojo.entity.Drama;
import com.zhenyu.pojo.entity.Episode;
import com.zhenyu.pojo.vo.DramaDetailVO;
import com.zhenyu.pojo.vo.DramaRecommendVO;
import com.zhenyu.pojo.vo.EpisodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DramaServiceImpl implements DramaService {
    @Autowired
    private DramaMapper dramaMapper;

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserUnlockMapper userUnlockMapper;

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

    @Override
    public List<DramaRecommendVO> recommend(Long categoryId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Drama> dramas = dramaMapper.listByCategoryId(categoryId);
        return dramas.stream().map(drama -> {
            Category category = categoryMapper.list(null).stream()
                    .filter(c -> c.getId().equals(drama.getCategoryId()))
                    .findFirst().orElse(null);
            return DramaRecommendVO.builder()
                    .id(drama.getId())
                    .title(drama.getTitle())
                    .coverUrl(drama.getCoverUrl())
                    .description(drama.getDescription())
                    .totalEpisodes(drama.getTotalEpisodes())
                    .views(drama.getViews())
                    .categoryName(category != null ? category.getName() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public DramaDetailVO getDetail(Long id, Long userId) {
        Drama drama = dramaMapper.getById(id);
        if (drama == null) {
            throw new RuntimeException("短剧不存在");
        }

        Category category = categoryMapper.list(null).stream()
                .filter(c -> c.getId().equals(drama.getCategoryId()))
                .findFirst().orElse(null);

        List<Episode> episodes = episodeMapper.listByDramaId(id);
        List<EpisodeVO> episodeVOs = episodes.stream().map(episode -> {
            Integer isUnlocked = 0;
            if (userId != null) {
                if (episode.getIsFree() == 1) {
                    isUnlocked = 1;
                } else {
                    Integer count = userUnlockMapper.countByUserIdAndDramaIdAndEpisodeId(userId, id, episode.getId());
                    if (count > 0) {
                        isUnlocked = 1;
                    }
                    if (userId != null) {
                        Integer wholeCount = userUnlockMapper.countWholeDrama(userId, id);
                        if (wholeCount > 0) {
                            isUnlocked = 1;
                        }
                    }
                }
            } else {
                if (episode.getIsFree() == 1) {
                    isUnlocked = 1;
                }
            }

            return EpisodeVO.builder()
                    .id(episode.getId())
                    .episodeNum(episode.getEpisodeNum())
                    .title(episode.getTitle())
                    .duration(episode.getDuration())
                    .isFree(episode.getIsFree())
                    .isUnlocked(isUnlocked)
                    .build();
        }).collect(Collectors.toList());

        // 判断是否已购买整部
        Integer purchased = 0;
        if (userId != null) {
            Integer wholeCount = userUnlockMapper.countWholeDrama(userId, id);
            if (wholeCount > 0) {
                purchased = 1;
            }
        }

        return DramaDetailVO.builder()
                .id(drama.getId())
                .title(drama.getTitle())
                .description(drama.getDescription())
                .coverUrl(drama.getCoverUrl())
                .categoryName(category != null ? category.getName() : null)
                .priceType(drama.getPriceType())
                .packagePrice(drama.getPackagePrice())
                .pricePerEpisode(drama.getPricePerEpisode())
                .episodes(episodeVOs)
                .purchased(purchased)
                .build();
    }
}
