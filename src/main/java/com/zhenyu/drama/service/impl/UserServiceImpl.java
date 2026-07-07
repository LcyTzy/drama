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
import com.zhenyu.pojo.dto.PhoneLoginDTO;
import com.zhenyu.pojo.dto.UserLoginDTO;
import com.zhenyu.pojo.dto.UserUpdateDTO;
import com.zhenyu.pojo.entity.User;
import com.zhenyu.pojo.vo.UserInfoVO;
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

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .nickname(userLoginDTO.getNickname())
                    .avatar(userLoginDTO.getAvatarUrl())
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        } else {
            user.setNickname(userLoginDTO.getNickname());
            user.setAvatar(userLoginDTO.getAvatarUrl());
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public void sendCode(String phone) {
        log.info("发送验证码到手机号: {}", phone);
        // TODO: 接入阿里云短信服务
    }

    @Override
    public User phoneLogin(PhoneLoginDTO phoneLoginDTO) {
        log.info("手机号登录: {}", phoneLoginDTO.getPhone());
        // TODO: 验证码校验逻辑
        User user = userMapper.getByPhone(phoneLoginDTO.getPhone());
        if (user == null) {
            user = User.builder()
                    .phone(phoneLoginDTO.getPhone())
                    .nickname("用户" + phoneLoginDTO.getPhone().substring(7))
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.getById(userId);
        return UserInfoVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public void updateUserInfo(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = User.builder()
                .id(userId)
                .nickname(userUpdateDTO.getNickname())
                .avatar(userUpdateDTO.getAvatar())
                .build();
        userMapper.updateById(user);
    }

    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getAppsecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
