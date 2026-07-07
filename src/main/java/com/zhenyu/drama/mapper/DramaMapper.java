package com.zhenyu.drama.mapper;

import com.github.pagehelper.Page;
import com.zhenyu.common.enumeration.OperationType;
import com.zhenyu.drama.annotation.AutoFill;
import com.zhenyu.pojo.dto.DramaPageQueryDTO;
import com.zhenyu.pojo.entity.Drama;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DramaMapper {

    // 根据分类id查询关联的短剧数量
    @Select("select count(*) from drama where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Insert("insert into drama (category_id, title, cover_url, description, director, actor, price_type, price_per_episode, package_price, free_episodes, status, views, create_time, update_time, create_user, update_user) " +
            "values (#{categoryId}, #{title}, #{coverUrl}, #{description}, #{director}, #{actor}, #{priceType}, #{pricePerEpisode}, #{packagePrice}, #{freeEpisodes}, #{status}, #{views}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Drama drama);

    @AutoFill(value = OperationType.UPDATE)
    void update(Drama drama);

    @Select("select * from drama where id = #{id}")
    Drama getById(Long id);

    Page<Drama> pageQuery(DramaPageQueryDTO dramaPageQueryDTO);

    @Delete("delete from drama where id = #{id}")
    void deleteById(Long id);

    @Select("<script>" +
            "select * from drama where status = 1" +
            "<if test='categoryId != null'> and category_id = #{categoryId}</if>" +
            " order by views desc, create_time desc" +
            "</script>")
    List<Drama> listByCategoryId(Long categoryId);
}
