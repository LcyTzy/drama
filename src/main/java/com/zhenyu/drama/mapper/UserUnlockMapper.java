package com.zhenyu.drama.mapper;

import com.zhenyu.pojo.entity.UserUnlock;
import com.zhenyu.pojo.vo.PurchasedVO;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserUnlockMapper {

    @Insert("insert into user_unlock (user_id, item_type, drama_id, episode_id, order_id, create_time) " +
            "values (#{userId}, #{itemType}, #{dramaId}, #{episodeId}, #{orderId}, #{createTime})")
    void insert(UserUnlock userUnlock);

    @Select("select count(*) from user_unlock where user_id = #{userId} and drama_id = #{dramaId} and episode_id = #{episodeId}")
    Integer countByUserIdAndDramaIdAndEpisodeId(@Param("userId") Long userId, @Param("dramaId") Long dramaId, @Param("episodeId") Long episodeId);

    @Select("select count(*) from user_unlock where user_id = #{userId} and drama_id = #{dramaId} and item_type = 1")
    Integer countWholeDrama(@Param("userId") Long userId, @Param("dramaId") Long dramaId);

    List<PurchasedVO> listPurchasedByUserId(@Param("userId") Long userId);

    @Select("select count(*) from user_unlock where user_id = #{userId} and drama_id = #{dramaId} and item_type = 2")
    Integer countSingleEpisodes(@Param("userId") Long userId, @Param("dramaId") Long dramaId);
}
