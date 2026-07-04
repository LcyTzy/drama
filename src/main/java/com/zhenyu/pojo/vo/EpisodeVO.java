package com.zhenyu.pojo.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EpisodeVO {
    private Long id;
    private Integer episodeNum;
    private String title;
    private Integer isFree;
    private Integer isUnlocked;      // 当前用户是否已解锁
}
