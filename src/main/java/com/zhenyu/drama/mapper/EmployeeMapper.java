package com.zhenyu.drama.mapper;

import com.github.pagehelper.Page;
import com.zhenyu.common.enumeration.OperationType;
import com.zhenyu.drama.annotation.AutoFill;
import com.zhenyu.pojo.dto.EmployeePageQueryDTO;
import com.zhenyu.pojo.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 根据id查询员工
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);


    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("insert into employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user, role)" +
            " values " +
            "(#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{role})")
    void insert(Employee employee);

    /**
     * 分页查询
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);
}
