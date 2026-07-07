package com.zhenyu.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ProgressDTO implements Serializable {
    private Long episodeId;
    private Integer currentSeconds;
}
