package com.zhenyu.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedVO {
    private Long dramaId;
    private String title;
    private String coverUrl;
    private String unlockType;
    private Integer unlockCount;
}
