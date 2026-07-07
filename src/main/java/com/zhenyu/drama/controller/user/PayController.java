package com.zhenyu.drama.controller.user;

import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.PayService;
import com.zhenyu.pojo.dto.CreateOrderDTO;
import com.zhenyu.pojo.vo.CreateOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/pay")
@Api(tags = "C端付费相关接口")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/createOrder")
    @ApiOperation("创建付费订单")
    public Result<CreateOrderVO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        log.info("创建付费订单，dramaId: {}, payMethod: {}", createOrderDTO.getDramaId(), createOrderDTO.getPayMethod());
        Long userId = BaseContext.getCurrentId();
        CreateOrderVO order = payService.createOrder(userId, createOrderDTO);
        return Result.success(order);
    }

    @PutMapping("/success")
    @ApiOperation("支付成功回调")
    public Result<String> paySuccess(@RequestBody java.util.Map<String, String> params) {
        String orderNo = params.get("orderNo");
        log.info("支付成功回调，orderNo: {}", orderNo);
        payService.paySuccess(orderNo);
        return Result.success();
    }
}
