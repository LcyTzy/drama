package com.zhenyu.pojo.dto;

//员工管理 DTO

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
}
