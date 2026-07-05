package com.zhenyu.drama.service;

import com.zhenyu.common.utils.PageResult;
import com.zhenyu.pojo.dto.EmployeeDTO;
import com.zhenyu.pojo.dto.EmployeeLoginDTO;
import com.zhenyu.pojo.dto.EmployeePageQueryDTO;
import com.zhenyu.pojo.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeService {
    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工信息
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 根据id查询员工信息
     * @param empId
     */
    Employee getById(Long empId);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, @Param("id") Long id);

    /**
     * 批量启用禁用员工账号
     * @param status
     * @param ids
     */
    void batchStatus(Integer status, List<Long> ids);

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     */
    void editPassword(String oldPassword, String newPassword);

    void deleteById(Long id);
}
