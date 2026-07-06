package com.zhenyu.drama.service;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.pojo.dto.OrderPageQueryDTO;
import com.zhenyu.pojo.vo.OrderDetailVO;
import com.zhenyu.pojo.vo.RevenueVO;

public interface OrderService {
    PageResult conditionSearch(OrderPageQueryDTO orderPageQueryDTO);

    OrderDetailVO getOrderDetailById(Long id);

    RevenueVO getRevenue(String begin, String end);
}
