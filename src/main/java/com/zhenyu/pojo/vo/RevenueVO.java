package com.zhenyu.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueVO {
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private Integer orderCount;
}
