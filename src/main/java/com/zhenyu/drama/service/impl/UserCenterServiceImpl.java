package com.zhenyu.drama.service.impl;

import com.zhenyu.drama.mapper.UserUnlockMapper;
import com.zhenyu.drama.service.UserCenterService;
import com.zhenyu.pojo.vo.PurchasedVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserCenterServiceImpl implements UserCenterService {

    @Autowired
    private UserUnlockMapper userUnlockMapper;

    @Override
    public List<PurchasedVO> getPurchased(Long userId) {
        return userUnlockMapper.listPurchasedByUserId(userId);
    }
}
