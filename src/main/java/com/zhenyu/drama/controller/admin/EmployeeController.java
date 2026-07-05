package com.zhenyu.drama.controller.admin;


import com.zhenyu.common.constant.JwtClaimsConstant;
import com.zhenyu.common.context.BaseContext;
import com.zhenyu.common.properties.JwtProperties;
import com.zhenyu.common.utils.JwtUtil;
import com.zhenyu.common.utils.PageResult;
import com.zhenyu.common.utils.Result;
import com.zhenyu.drama.mapper.EmployeeMapper;
import com.zhenyu.drama.service.EmployeeService;
import com.zhenyu.pojo.dto.EmployeeDTO;
import com.zhenyu.pojo.dto.EmployeeLoginDTO;
import com.zhenyu.pojo.dto.EmployeePageQueryDTO;
import com.zhenyu.pojo.dto.PasswordDTO;
import com.zhenyu.pojo.entity.Employee;
import com.zhenyu.pojo.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */

@RestController
@RequestMapping("/admin")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EmployeeMapper employeeMapper;

    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("开始员工登录...");
        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    @ApiOperation(value = "员工信息")
    public Result<Employee> getEmployeeInfo() {
        Long empId = BaseContext.getCurrentId();
        Employee employee = employeeService.getById(empId);
        return Result.success(employee);
    }


    @PostMapping("/employee")
    @ApiOperation(value = "新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/employee/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/employee")
    @ApiOperation(value = "编辑员工")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @PostMapping("/employee/status/{status}")
    @ApiOperation(value = "启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    @PostMapping("/employee/batchStatus/{status}")
    @ApiOperation(value = "批量启用禁用员工账号")
    public Result batchStatus(@PathVariable Integer status, @RequestBody List<Long> ids) {
        employeeService.batchStatus(status, ids);
        return Result.success();
    }

    

    @GetMapping("/employee/{id}")
    @ApiOperation(value = "根据id查询员工")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping("/employee/editPassword")
    @ApiOperation(value = "修改密码")
    public Result<String> editPassword(@RequestBody PasswordDTO passwordDTO) {
        employeeService.editPassword(passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success();
    }

    @DeleteMapping("/employee")
    @ApiOperation(value = "删除员工")
    public Result<String> delete(Long id) {
        employeeService.deleteById(id);
        return Result.success();
    }

}
