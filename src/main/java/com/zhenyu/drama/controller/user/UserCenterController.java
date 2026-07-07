package com.zhenyu.drama.controller.user;

import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.UserCenterService;
import com.zhenyu.drama.service.UserService;
import com.zhenyu.pojo.dto.UserUpdateDTO;
import com.zhenyu.pojo.vo.PurchasedVO;
import com.zhenyu.pojo.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/center")
@Api(tags = "C端用户中心相关接口")
@Slf4j
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private UserService userService;

    @GetMapping("/purchased")
    @ApiOperation("获取已购短剧列表")
    public Result<List<PurchasedVO>> purchased() {
        log.info("获取已购短剧列表");
        Long userId = BaseContext.getCurrentId();
        List<PurchasedVO> list = userCenterService.getPurchased(userId);
        return Result.success(list);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户基本信息")
    public Result<UserInfoVO> getInfo() {
        log.info("获取用户基本信息");
        Long userId = BaseContext.getCurrentId();
        UserInfoVO info = userService.getUserInfo(userId);
        return Result.success(info);
    }

    @PutMapping("/info")
    @ApiOperation("修改用户信息")
    public Result<String> updateInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("修改用户信息，nickname: {}, avatar: {}", userUpdateDTO.getNickname(), userUpdateDTO.getAvatar());
        Long userId = BaseContext.getCurrentId();
        userService.updateUserInfo(userId, userUpdateDTO);
        return Result.success();
    }
}
