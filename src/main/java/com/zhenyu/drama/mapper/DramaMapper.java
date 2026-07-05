package com.zhenyu.drama.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DramaMapper {

    // 根据分类id查询关联的短剧数量
    @Select("select count(*) from drama where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
