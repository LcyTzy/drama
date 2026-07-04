package com.zhenyu.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

// 短剧 DTO

@Data
public class DramaDTO {
    private Long id;
    private Long categoryId;
    private String title;
    private String coverUrl;
    private String description;
    private String director;
    private String actor;
    private Integer priceType;
    private BigDecimal pricePerEpisode;
    private BigDecimal packagePrice;
    private Integer freeEpisodes;
}
