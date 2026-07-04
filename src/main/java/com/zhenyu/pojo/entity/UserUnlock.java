package com.zhenyu.pojo.entity;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//用户解锁记录（鉴权核心）

@Data
@Builder
public class UserUnlock {
    private Long id;
    private Long userId;
    private Integer itemType;        // 1整部剧 2单集
    private Long dramaId;
    private Long episodeId;
    private Long orderId;
    private LocalDateTime createTime;
}