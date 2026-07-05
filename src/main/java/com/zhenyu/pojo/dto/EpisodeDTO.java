package com.zhenyu.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

// 剧集 DTO

@Data
public class EpisodeDTO {
    private Long id;
    private Long dramaId;
    private Integer episodeNum;
    private String title;
    private String videoUrl;
    private Integer duration;
    private Long fileSize;
    private Integer isFree;
    private Integer sort;
}
