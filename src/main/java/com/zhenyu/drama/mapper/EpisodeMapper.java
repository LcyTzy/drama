package com.zhenyu.drama.mapper;

import com.zhenyu.common.enumeration.OperationType;
import com.zhenyu.drama.annotation.AutoFill;
import com.zhenyu.pojo.entity.Episode;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EpisodeMapper {

    @Insert("insert into episode (drama_id, episode_num, title, video_url, duration, file_size, is_free, sort, create_time, update_time) " +
            "values (#{dramaId}, #{episodeNum}, #{title}, #{videoUrl}, #{duration}, #{fileSize}, #{isFree}, #{sort}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Episode episode);

    @AutoFill(value = OperationType.UPDATE)
    void update(Episode episode);

    @Select("select * from episode where drama_id = #{dramaId} order by episode_num asc")
    List<Episode> listByDramaId(Long dramaId);

    @Select("select * from episode where id = #{id}")
    Episode getById(Long id);

    @Delete("delete from episode where id = #{id}")
    void deleteById(Long id);

    @Select("select count(*) from episode where drama_id = #{dramaId}")
    Integer countByDramaId(Long dramaId);
}
