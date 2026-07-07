package com.zhenyu.drama.mapper;

import com.zhenyu.pojo.vo.DanmakuVO;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DanmakuMapper {

    @Insert("insert into danmaku (user_id, episode_id, content, timestamp, color, create_time) values (#{userId}, #{episodeId}, #{content}, #{timestamp}, #{color}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(com.zhenyu.pojo.entity.Danmaku danmaku);

    @Delete("delete from danmaku where id = #{id} and user_id = #{userId}")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<DanmakuVO> listByEpisodeId(@Param("episodeId") Long episodeId, @Param("startTime") Integer startTime, @Param("endTime") Integer endTime);
}
