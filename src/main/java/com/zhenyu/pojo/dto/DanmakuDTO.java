package com.zhenyu.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DanmakuDTO implements Serializable {
    private Long episodeId;
    private String content;
    private Integer timestamp;
    private String color;
}
