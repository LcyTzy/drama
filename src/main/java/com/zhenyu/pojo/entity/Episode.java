package com.zhenyu.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//剧集实体

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Episode {
    private Long id;
    private Long dramaId;
    private Integer episodeNum;
    private String title;
    private String videoUrl;
    private Integer duration;         // 时长（秒）
    private Long fileSize;            // 文件大小（字节）
    private Integer isFree;           // 1免费 0付费
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}