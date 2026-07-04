package com.zhenyu.drama.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenyu.drama.mapper.UserMapper;
import com.zhenyu.drama.service.UserService;
import com.zhenyu.pojo.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
