package com.insightflow.vo;

import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.sys.Menu;
import com.insightflow.entity.sys.Role;
import com.insightflow.entity.sys.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-08-09
 * @Description: 登录信息
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginInfoVO extends User {

    private EmployeeInfo employeeInfo;

    private Role role;

    private List<Menu> menus;
}
