package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayUrlVO {
    private String videoUrl;
    private String expireAt;
    private Integer duration;
}
