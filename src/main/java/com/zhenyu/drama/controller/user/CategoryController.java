package com.zhenyu.drama.controller.user;

import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.CategoryService;
import com.zhenyu.pojo.entity.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("查询分类列表")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        log.info("查询分类列表，type: {}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
