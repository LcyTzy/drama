package com.zhenyu.drama.service;

import com.zhenyu.pojo.dto.CreateOrderDTO;
import com.zhenyu.pojo.vo.CreateOrderVO;

public interface PayService {
    CreateOrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO);

    void paySuccess(String orderNo);
}
