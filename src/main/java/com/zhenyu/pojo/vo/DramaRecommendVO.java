package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DramaRecommendVO {
    private Long id;
    private String title;
    private String coverUrl;
    private String description;
    private Integer totalEpisodes;
    private Long views;
    private String categoryName;
}
