package com.zhenyu.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Danmaku {
    private Long id;
    private Long userId;
    private Long episodeId;
    private String content;
    private Integer timestamp;
    private String color;
    private Integer fontSize;
    private LocalDateTime createTime;
}
