package com.zhenyu.drama.controller.user;

import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.DanmakuService;
import com.zhenyu.pojo.dto.DanmakuDTO;
import com.zhenyu.pojo.vo.DanmakuVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/danmaku")
@Api(tags = "C端弹幕相关接口")
@Slf4j
public class DanmakuController {

    @Autowired
    private DanmakuService danmakuService;

    @GetMapping("/list")
    @ApiOperation("拉取弹幕列表")
    public Result<List<DanmakuVO>> list(
            @RequestParam Long episodeId,
            @RequestParam(required = false) Integer startTime,
            @RequestParam(required = false) Integer endTime) {
        log.info("拉取弹幕列表，episodeId: {}, startTime: {}, endTime: {}", episodeId, startTime, endTime);
        List<DanmakuVO> list = danmakuService.list(episodeId, startTime, endTime);
        return Result.success(list);
    }

    @PostMapping("/send")
    @ApiOperation("发送弹幕")
    public Result<String> send(@RequestBody DanmakuDTO danmakuDTO) {
        log.info("发送弹幕，episodeId: {}, content: {}", danmakuDTO.getEpisodeId(), danmakuDTO.getContent());
        Long userId = BaseContext.getCurrentId();
        danmakuService.send(userId, danmakuDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除弹幕")
    public Result<String> delete(@RequestParam Long id) {
        log.info("删除弹幕，id: {}", id);
        Long userId = BaseContext.getCurrentId();
        danmakuService.delete(userId, id);
        return Result.success();
    }
}
