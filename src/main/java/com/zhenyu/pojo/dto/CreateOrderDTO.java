package com.zhenyu.pojo.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CreateOrderDTO implements Serializable {
    private Long dramaId;
    private List<Long> episodeIds;
    private Integer payMethod;
}
