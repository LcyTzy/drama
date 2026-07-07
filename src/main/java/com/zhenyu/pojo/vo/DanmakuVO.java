package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanmakuVO {
    private Long id;
    private String content;
    private Integer timestamp;
    private String color;
    private String nickname;
}
