package com.zhenyu.drama.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenyu.common.constant.MessageConstant;
import com.zhenyu.common.exception.LoginFailedException;
import com.zhenyu.common.properties.WeChatProperties;
import com.zhenyu.common.utils.HttpClientUtil;
import com.zhenyu.drama.mapper.UserMapper;
import com.zhenyu.drama.service.UserService;
import com.zhenyu.pojo.dto.UserLoginDTO;
import com.zhenyu.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;


    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断openid是否为空
        if (openid != null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);


        }

        // 判断当前微信用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        // 是新用户，自动完成注册

        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);

        }
        // 返回用户信息
        return user;
    }


    private String getOpenid(String code) {
        // 调用微信接口服务，获取当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
