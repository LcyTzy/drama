package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DramaDetailVO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String categoryName;
    private Integer priceType;
    private BigDecimal packagePrice;
    private BigDecimal pricePerEpisode;
    private List<EpisodeVO> episodes;
    private Integer purchased; // 是否已购买整部 0-否 1-是
}
