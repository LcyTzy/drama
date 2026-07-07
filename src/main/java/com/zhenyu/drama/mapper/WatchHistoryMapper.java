package com.zhenyu.drama.mapper;

import com.zhenyu.pojo.entity.WatchHistory;
import com.zhenyu.pojo.vo.HistoryVO;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface WatchHistoryMapper {

    @Insert("insert into watch_history (user_id, episode_id, progress_seconds, duration, is_finished, update_time) " +
            "values (#{userId}, #{episodeId}, #{progressSeconds}, #{duration}, #{isFinished}, #{updateTime}) " +
            "on duplicate key update progress_seconds = #{progressSeconds}, duration = #{duration}, is_finished = #{isFinished}, update_time = #{updateTime}")
    void upsert(WatchHistory watchHistory);

    List<HistoryVO> listByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
