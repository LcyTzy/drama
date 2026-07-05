package com.zhenyu.pojo.dto;

import lombok.Data;

@Data
public class DramaPageQueryDTO {
    private String title;
    private Long categoryId;
    private Integer status;
    private Integer page;
    private Integer pageSize;
}
