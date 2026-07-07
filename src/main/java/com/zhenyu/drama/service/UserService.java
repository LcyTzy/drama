package com.zhenyu.drama.service;

import com.zhenyu.pojo.dto.PhoneLoginDTO;
import com.zhenyu.pojo.dto.UserLoginDTO;
import com.zhenyu.pojo.dto.UserUpdateDTO;
import com.zhenyu.pojo.entity.User;
import com.zhenyu.pojo.vo.UserInfoVO;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    void sendCode(String phone);

    User phoneLogin(PhoneLoginDTO phoneLoginDTO);

    UserInfoVO getUserInfo(Long userId);

    void updateUserInfo(Long userId, UserUpdateDTO userUpdateDTO);
}
