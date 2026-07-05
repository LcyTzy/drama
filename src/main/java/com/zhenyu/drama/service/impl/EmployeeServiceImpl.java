package com.zhenyu.drama.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhenyu.common.constant.MessageConstant;
import com.zhenyu.common.constant.PasswordConstant;
import com.zhenyu.common.constant.StatusConstant;
import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.exception.*;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.drama.mapper.EmployeeMapper;
import com.zhenyu.drama.service.EmployeeService;
import com.zhenyu.pojo.dto.EmployeeDTO;
import com.zhenyu.pojo.dto.EmployeeLoginDTO;
import com.zhenyu.pojo.dto.EmployeePageQueryDTO;
import com.zhenyu.pojo.entity.Employee;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // 进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if (employee.getStatus() == StatusConstant.DISABLE) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);

        }

        // 3、返回实体对象
        return employee;

    }


    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
       Employee employee = new Employee();
       // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.insert(employee);
    }

    /**
     * 根据ID查询员工
     * @param empId
     * @return
     */
    @Override
    public Employee getById(Long empId) {
       return employeeMapper.getById(empId);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.update(employee);
    }

    @Override
    public void batchStatus(Integer status, List<Long> ids) {
        for (Long id : ids) {
            Employee employee = Employee.builder().status(status).id(id).build();
            employeeMapper.update(employee);
        }
    }

    @Override
    public void editPassword(String oldPassword, String newPassword) {
        Long empId = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(empId);

        // 验证旧密码
        String oldPasswordMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldPasswordMd5.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 新密码加密后更新
        String newPasswordMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        employeeMapper.updatePassword(empId, newPasswordMd5);
    }

    @Override
    public void deleteById(Long id) {
        Employee employee = employeeMapper.getById(id);
        if ("admin".equals(employee.getRole())) {
            throw new DeleteAdminException(MessageConstant.CANNOT_DELETE_ADMIN);
        }
        employeeMapper.deleteById(id);
    }
}
