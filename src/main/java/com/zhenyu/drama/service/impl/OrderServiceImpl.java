package com.zhenyu.drama.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.drama.mapper.OrderMapper;
import com.zhenyu.drama.service.OrderService;
import com.zhenyu.pojo.dto.OrderPageQueryDTO;
import com.zhenyu.pojo.vo.OrderDetailVO;
import com.zhenyu.pojo.vo.OrderVO;
import com.zhenyu.pojo.vo.RevenueVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public PageResult conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.conditionSearch(orderPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public OrderDetailVO getOrderDetailById(Long id) {
        return orderMapper.getOrderDetailById(id);
    }

    @Override
    public RevenueVO getRevenue(String begin, String end) {
        RevenueVO revenueVO = new RevenueVO();
        if (begin != null && end != null) {
            revenueVO.setTotalRevenue(orderMapper.getRevenue(begin, end));
            revenueVO.setOrderCount(orderMapper.getOrderCount(begin, end));
        } else {
            revenueVO.setTotalRevenue(orderMapper.getRevenue(null, null));
            revenueVO.setOrderCount(orderMapper.getOrderCount(null, null));
        }
        revenueVO.setTodayRevenue(orderMapper.getTodayRevenue());
        return revenueVO;
    }
}
