package com.flow.service;

import com.flow.dto.EmployeeDTO;
import com.flow.dto.EmployeeLoginDTO;
import com.flow.dto.EmployeePageQueryDTO;
import com.flow.entity.Employee;
import com.flow.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 根据id修改员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);
}
