package com.zhenyu.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//用户实体（C端）

@Data
@Builder
@NoArgsConstructor
@TableName("user")
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String phone;
    private String nickname;
    private String avatar;
    private String sex;
    private LocalDateTime createTime;
}