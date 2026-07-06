package com.zhenyu.pojo.dto;

import lombok.Data;

@Data
public class OrderPageQueryDTO {
    private String orderNo;
    private String userPhone;
    private Integer status;
    private String beginTime;
    private String endTime;
    private int page;
    private int pageSize;
}
