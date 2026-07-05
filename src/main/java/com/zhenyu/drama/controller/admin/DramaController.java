package com.zhenyu.drama.controller.admin;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.DramaService;
import com.zhenyu.pojo.dto.DramaDTO;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Drama;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drama")
@Slf4j
@Api(tags = "短剧管理")
public class DramaController {
    @Autowired
    private DramaService dramaService;

    @PostMapping
    @ApiOperation("新增短剧")
    public Result<String> save(@RequestBody DramaDTO dramaDTO) {
        dramaService.save(dramaDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改短剧")
    public Result<String> update(@RequestBody DramaDTO dramaDTO) {
        dramaService.update(dramaDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("短剧详情")
    public Result<Drama> getById(@PathVariable Long id) {
        Drama drama = dramaService.getById(id);
        return Result.success(drama);
    }

    @GetMapping("/page")
    @ApiOperation("短剧分页查询")
    public Result<PageResult> page(DramaPageQueryDTO dramaPageQueryDTO) {
        PageResult pageResult = dramaService.pageQuery(dramaPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("上下架短剧")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dramaService.startOrStop(status, id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除短剧")
    public Result<String> deleteById(Long id) {
        dramaService.deleteById(id);
        return Result.success();
    }
}
