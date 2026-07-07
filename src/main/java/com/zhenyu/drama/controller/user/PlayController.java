package com.zhenyu.drama.controller.user;

import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.PlayService;
import com.zhenyu.pojo.dto.ProgressDTO;
import com.zhenyu.pojo.vo.HistoryVO;
import com.zhenyu.pojo.vo.PlayUrlVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/play")
@Api(tags = "C端播放相关接口")
@Slf4j
public class PlayController {

    @Autowired
    private PlayService playService;

    @GetMapping("/getUrl")
    @ApiOperation("获取播放地址")
    public Result<PlayUrlVO> getUrl(@RequestParam Long episodeId) {
        log.info("获取播放地址，episodeId: {}", episodeId);
        Long userId = BaseContext.getCurrentId();
        PlayUrlVO playUrl = playService.getPlayUrl(episodeId, userId);
        return Result.success(playUrl);
    }

    @PostMapping("/progress")
    @ApiOperation("同步播放进度")
    public Result<String> syncProgress(@RequestBody ProgressDTO progressDTO) {
        log.info("同步播放进度，episodeId: {}, currentSeconds: {}", progressDTO.getEpisodeId(), progressDTO.getCurrentSeconds());
        Long userId = BaseContext.getCurrentId();
        playService.syncProgress(userId, progressDTO);
        return Result.success();
    }

    @GetMapping("/history")
    @ApiOperation("获取观看历史")
    public Result<List<HistoryVO>> history(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取观看历史，page: {}, pageSize: {}", page, pageSize);
        Long userId = BaseContext.getCurrentId();
        List<HistoryVO> history = playService.getHistory(userId, page, pageSize);
        return Result.success(history);
    }
}
