package com.zhenyu.drama.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid (String openid);

    @Select("select * from user where id=#{id}")
    User getById(Long id);

    @Select("select * from user where phone=#{phone}")
    User getByPhone(String phone);
}
