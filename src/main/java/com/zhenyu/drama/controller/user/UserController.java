package com.zhenyu.drama.controller.user;

import com.zhenyu.common.constant.JwtClaimsConstant;
import com.zhenyu.common.properties.JwtProperties;
import com.zhenyu.common.utils.JwtUtil;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.UserService;
import com.zhenyu.pojo.dto.PhoneLoginDTO;
import com.zhenyu.pojo.dto.UserLoginDTO;
import com.zhenyu.pojo.entity.User;
import com.zhenyu.pojo.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/user/auth")
@RestController
@Api(tags = "C端用户认证相关接口")
@Slf4j
public class UserController {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信登录，code: {}", userLoginDTO.getCode());
        User user = userService.wxLogin(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @PostMapping("/sendCode")
    @ApiOperation("发送手机验证码")
    public Result<String> sendCode(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        log.info("发送验证码，phone: {}", phone);
        userService.sendCode(phone);
        return Result.success();
    }

    @PostMapping("/loginByPhone")
    @ApiOperation("手机号验证码登录")
    public Result<UserLoginVO> loginByPhone(@RequestBody PhoneLoginDTO phoneLoginDTO) {
        log.info("手机号登录，phone: {}", phoneLoginDTO.getPhone());
        User user = userService.phoneLogin(phoneLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Result<String> logout() {
        return Result.success();
    }
}
