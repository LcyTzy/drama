package com.zhenyu.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long dramaId;
    private String dramaTitle;
    private String episodeIds;
    private BigDecimal totalAmount;
    private Integer payMethod;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
