package com.zhenyu.drama.controller.admin;


import com.zhenyu.common.utils.PageResult;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.service.CategoryService;
import com.zhenyu.pojo.dto.CategoryDTO;
import com.zhenyu.pojo.dto.CategoryPageQueryDTO;
import com.zhenyu.pojo.entity.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页分类查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起停分类")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }
}
