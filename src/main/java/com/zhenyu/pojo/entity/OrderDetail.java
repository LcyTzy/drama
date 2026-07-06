package com.zhenyu.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long orderId;
    private Integer itemType;
    private Long dramaId;
    private Long episodeId;
    private String itemName;
    private BigDecimal price;
    private LocalDateTime createTime;
}
