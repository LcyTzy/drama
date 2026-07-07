package com.zhenyu.drama.service.impl;

import com.zhenyu.drama.mapper.DramaMapper;
import com.zhenyu.drama.mapper.EpisodeMapper;
import com.zhenyu.drama.mapper.OrderMapper;
import com.zhenyu.drama.mapper.UserUnlockMapper;
import com.zhenyu.drama.service.PayService;
import com.zhenyu.pojo.dto.CreateOrderDTO;
import com.zhenyu.pojo.entity.Drama;
import com.zhenyu.pojo.entity.Episode;
import com.zhenyu.pojo.entity.Order;
import com.zhenyu.pojo.entity.OrderDetail;
import com.zhenyu.pojo.entity.UserUnlock;
import com.zhenyu.pojo.vo.CreateOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private DramaMapper dramaMapper;

    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserUnlockMapper userUnlockMapper;

    @Override
    @Transactional
    public CreateOrderVO createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        if (createOrderDTO.getDramaId() == null) {
            throw new RuntimeException("短剧ID不能为空");
        }

        Drama drama = dramaMapper.getById(createOrderDTO.getDramaId());
        if (drama == null) {
            throw new RuntimeException("短剧不存在");
        }

        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        BigDecimal totalAmount = BigDecimal.ZERO;
        Integer itemType;
        String itemName;

        if (createOrderDTO.getEpisodeIds() == null || createOrderDTO.getEpisodeIds().isEmpty()) {
            itemType = 1;
            totalAmount = drama.getPackagePrice();
            if (totalAmount == null) {
                throw new RuntimeException("短剧未设置整部购买价格");
            }
            itemName = drama.getTitle() + "（整部买断）";
        } else {
            itemType = 2;
            BigDecimal pricePerEpisode = drama.getPricePerEpisode();
            if (pricePerEpisode == null) {
                throw new RuntimeException("短剧未设置单集价格");
            }
            totalAmount = pricePerEpisode.multiply(BigDecimal.valueOf(createOrderDTO.getEpisodeIds().size()));
            itemName = drama.getTitle() + "（单集解锁）";
        }

        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .totalAmount(totalAmount)
                .payMethod(createOrderDTO.getPayMethod())
                .payStatus(0)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        orderMapper.insert(order);

        if (itemType == 1) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(order.getId())
                    .itemType(1)
                    .dramaId(drama.getId())
                    .itemName(itemName)
                    .price(totalAmount)
                    .createTime(LocalDateTime.now())
                    .build();
            orderMapper.insertDetail(orderDetail);
        } else {
            for (Long episodeId : createOrderDTO.getEpisodeIds()) {
                Episode episode = episodeMapper.getById(episodeId);
                OrderDetail orderDetail = OrderDetail.builder()
                        .orderId(order.getId())
                        .itemType(2)
                        .dramaId(drama.getId())
                        .episodeId(episodeId)
                        .itemName(itemName)
                        .price(drama.getPricePerEpisode())
                        .createTime(LocalDateTime.now())
                        .build();
                orderMapper.insertDetail(orderDetail);
            }
        }

        return CreateOrderVO.builder()
                .orderNo(orderNo)
                .payAmount(totalAmount)
                .prepayId("PREPAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
    }

    @Override
    @Transactional
    public void paySuccess(String orderNo) {
        Order order = orderMapper.getByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setPayStatus(1);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.update(order);

        List<OrderDetail> details = orderMapper.getDetailsByOrderId(order.getId());
        for (OrderDetail detail : details) {
            if (detail.getItemType() == 1) {
                UserUnlock unlock = UserUnlock.builder()
                        .userId(order.getUserId())
                        .itemType(1)
                        .dramaId(detail.getDramaId())
                        .orderId(order.getId())
                        .createTime(LocalDateTime.now())
                        .build();
                userUnlockMapper.insert(unlock);
            } else {
                UserUnlock unlock = UserUnlock.builder()
                        .userId(order.getUserId())
                        .itemType(2)
                        .dramaId(detail.getDramaId())
                        .episodeId(detail.getEpisodeId())
                        .orderId(order.getId())
                        .createTime(LocalDateTime.now())
                        .build();
                userUnlockMapper.insert(unlock);
            }
        }
    }
}
