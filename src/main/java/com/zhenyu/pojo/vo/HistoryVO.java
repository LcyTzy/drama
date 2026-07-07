package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryVO {
    private Long dramaId;
    private String dramaTitle;
    private String coverUrl;
    private Integer episodeNum;
    private String progress;
    private String updateTime;
}
