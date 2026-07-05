package com.zhenyu.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//短剧实体

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drama {
    private Long id;
    private Long categoryId;
    private String title;
    private String coverUrl;
    private String description;
    private String director;
    private String actor;
    private Integer totalEpisodes;
    private Integer priceType;           // 1按集解锁 2整部买断
    private BigDecimal pricePerEpisode;
    private BigDecimal packagePrice;
    private Integer freeEpisodes;
    private Integer status;              // 1上架 0下架
    private Long views;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}