package com.zhenyu.drama.mapper;

import com.github.pagehelper.Page;
import com.zhenyu.common.enumeration.OperationType;
import com.zhenyu.drama.annotation.AutoFill;
import com.zhenyu.pojo.dto.CategoryPageQueryDTO;
import com.zhenyu.pojo.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("insert into category (name, type, sort, create_time, update_time) values (#{name}, #{type}, #{sort}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    // 根据id删除分类
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @Select("select * from category where type = #{type} order by sort asc, create_time asc")
    List<Category> list(Integer type);
}
