package com.zhenyu.drama.controller.admin;


import com.zhenyu.common.utils.PageResult;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.OrderService;
import com.zhenyu.pojo.dto.OrderPageQueryDTO;
import com.zhenyu.pojo.vo.OrderDetailVO;
import com.zhenyu.pojo.vo.RevenueVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(orderPageQueryDTO);
        return Result.success(pageResult);
    }


    @GetMapping("/{id}")
    @ApiOperation("订单详情")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.getOrderDetailById(id);
        return Result.success(orderDetailVO);
    }

    @GetMapping("/revenue")
    @ApiOperation("平台收入统计")
    public Result<RevenueVO> getRevenue(
            @RequestParam(required = false) String begin,
            @RequestParam(required = false) String end) {
        RevenueVO revenueVO = orderService.getRevenue(begin, end);
        return Result.success(revenueVO);
    }
}
