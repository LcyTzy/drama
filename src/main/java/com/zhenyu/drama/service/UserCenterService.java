package com.zhenyu.drama.service;

import com.zhenyu.pojo.vo.PurchasedVO;

import java.util.List;

public interface UserCenterService {
    List<PurchasedVO> getPurchased(Long userId);
}
