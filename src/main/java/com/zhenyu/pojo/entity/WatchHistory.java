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
public class WatchHistory {
    private Long id;
    private Long userId;
    private Long episodeId;
    private Integer progressSeconds;
    private Integer duration;
    private Integer isFinished;
    private LocalDateTime updateTime;
}
