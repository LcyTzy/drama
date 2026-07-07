package com.zhenyu.drama.controller.user;

import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.DramaService;
import com.zhenyu.pojo.vo.DramaDetailVO;
import com.zhenyu.pojo.vo.DramaRecommendVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDramaController")
@RequestMapping("/user/drama")
@Api(tags = "C端短剧相关接口")
@Slf4j
public class DramaController {

    @Autowired
    private DramaService dramaService;

    @GetMapping("/recommend")
    @ApiOperation("首页推荐短剧列表")
    public Result<List<DramaRecommendVO>> recommend(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("首页推荐短剧列表，categoryId: {}, page: {}, pageSize: {}", categoryId, page, pageSize);
        List<DramaRecommendVO> list = dramaService.recommend(categoryId, page, pageSize);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("短剧详情")
    public Result<DramaDetailVO> detail(@PathVariable Long id) {
        log.info("短剧详情，id: {}", id);
        Long userId = BaseContext.getCurrentId();
        DramaDetailVO detail = dramaService.getDetail(id, userId);
        return Result.success(detail);
    }
}
