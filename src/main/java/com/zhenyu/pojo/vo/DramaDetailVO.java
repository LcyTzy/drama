package com.zhenyu.pojo.vo;

//短剧详情 VO（含剧集列表和解锁状态）

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DramaDetailVO {
    private Long id;
    private String title;
    private String coverUrl;
    private String description;
    private Integer priceType;
    private BigDecimal packagePrice;
    private List<EpisodeVO> episodes;
}

