package com.zhenyu.drama.controller.user;

import com.zhenyu.common.constant.JwtClaimsConstant;
import com.zhenyu.common.properties.JwtProperties;
import com.zhenyu.common.utils.JwtUtil;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.UserService;
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

@RequestMapping("/uer/uer")
@RestController
@Api(tags = "C端用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("微信登陆")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        User user = userService.wxLogin(userLoginDTO);

        // 生成jwt令牌
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
}
