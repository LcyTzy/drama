package com.zhenyu.drama.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhenyu.pojo.dto.UserLoginDTO;
import com.zhenyu.pojo.entity.User;

public interface UserService{
    /**
     * 微信登陆
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
