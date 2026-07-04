package com.zhenyu.drama.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
