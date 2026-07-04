package com.zhenyu.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeePageQueryDTO implements Serializable {

    // 员工姓名
    private String name;

    // 当前页码
    private int page;

    // 每页显示数量
    private int pageSize;
}
