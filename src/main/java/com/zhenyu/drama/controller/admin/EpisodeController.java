package com.zhenyu.drama.controller.admin;

import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.EpisodeService;
import com.zhenyu.pojo.dto.EpisodeDTO;
import com.zhenyu.pojo.entity.Episode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/episode")
@Slf4j
@Api(tags = "剧集管理")
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @PostMapping
    @ApiOperation("上传/新增剧集")
    public Result<String> save(
            @RequestParam Long dramaId,
            @RequestParam Integer episodeNum,
            @RequestParam(required = false) String title,
            @RequestParam MultipartFile videoFile,
            @RequestParam(defaultValue = "0") Integer isFree) {

        EpisodeDTO episodeDTO = new EpisodeDTO();
        episodeDTO.setDramaId(dramaId);
        episodeDTO.setEpisodeNum(episodeNum);
        episodeDTO.setTitle(title);
        episodeDTO.setIsFree(isFree);
        episodeDTO.setSort(episodeNum);

        episodeService.save(episodeDTO, videoFile);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据短剧ID查询剧集列表")
    public Result<List<Episode>> list(Long dramaId) {
        List<Episode> list = episodeService.listByDramaId(dramaId);
        return Result.success(list);
    }

    @PutMapping
    @ApiOperation("更新剧集信息")
    public Result<String> update(@RequestBody EpisodeDTO episodeDTO) {
        episodeService.update(episodeDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除剧集")
    public Result<String> deleteById(Long id) {
        episodeService.deleteById(id);
        return Result.success();
    }
}
