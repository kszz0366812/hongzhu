package com.insightflow.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.service.sys.RoleService;
import com.insightflow.vo.LoginInfoVO;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.sys.User;
import com.insightflow.mapper.sys.UserMapper;
import com.insightflow.service.employee.EmployeeInfoService;
import com.insightflow.service.sys.UserService;
import com.insightflow.common.util.JwtUtils;
import com.insightflow.common.util.EntityUtils;
import com.insightflow.service.common.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.ArrayList;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Autowired
    RoleService roleService;
    
    @Autowired
    AttachmentService attachmentService;

    @Override
    public String login(String username, String password) {
        User user = getByUsername(username);
        if (user != null && user.getPassword().equals(password)&&user.getEmployeeId()!=null&&user.getStatus()==1) {
            // 生成JWT token
            return  JwtUtils.generateToken(generateLoginInfo(user));
        }
        return null;
    }
    
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            return false;
        }
        return save(user);
    }
    
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = getByUsername(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            return updateById(user);
        }
        return false;
    }
    
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public LoginInfoVO generateLoginInfo(User user) {
        EmployeeInfo employeeInfo = employeeInfoService.getById(user.getEmployeeId());
        LoginInfoVO loginInfo = new LoginInfoVO();
        loginInfo.setId(user.getId());
        loginInfo.setUsername(user.getUsername());
        loginInfo.setRealName(user.getRealName());
        loginInfo.setMobile(user.getMobile());
        loginInfo.setEmail(user.getEmail());
        loginInfo.setOpenid(user.getOpenid());
        loginInfo.setUnionid(user.getUnionid());
        loginInfo.setNickname(user.getNickname());
        loginInfo.setAvatarUrl(user.getAvatarUrl());
        loginInfo.setEmployeeId(user.getEmployeeId());
        loginInfo.setEmployeeInfo(employeeInfo);

        return loginInfo;
    }

    @Override
    public IPage<User> getUserPage(Integer pageNum, Integer pageSize, String username, String realName, String mobile, String email, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (StringUtils.hasText(mobile)) {
            wrapper.like(User::getMobile, mobile);
        }
        if (StringUtils.hasText(email)) {
            wrapper.like(User::getEmail, email);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        // 获取用户分页数据
        IPage<User> userPage = this.page(page, wrapper);
        
        // 为每个用户关联员工信息
        if (userPage.getRecords() != null) {
            for (User user : userPage.getRecords()) {
                if (user.getEmployeeId() != null) {
                    EmployeeInfo employeeInfo = employeeInfoService.getById(user.getEmployeeId());
                    if (employeeInfo != null) {
                        user.setEmployeeInfo(employeeInfo);
                    }
                }
            }
        }
        
        return userPage;
    }

    @Override
        public User addUser(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            return null;
        }
        // 设置默认密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(user.getPassword());
        } else {
            user.setPassword("123456"); // 默认密码
        }
        // 自动设置创建信息
        EntityUtils.setCreateInfo(user);
        save(user);
        return user;
    }
    


    @Override
    public String updateUser(User user) {
        // 检查用户名是否已存在（排除自己）
        User existingUser = getByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return null;
        }
        // 自动设置更新信息
        EntityUtils.setUpdateInfo(user);
        updateById(user);
        //如果是自己修改信息则重新生成 token
        if (JwtUtils.getLoginInfo().getId().equals(user.getId())) {
            User dbUser = getById(user.getId());
            return JwtUtils.generateToken(generateLoginInfo(dbUser));
        }
        return null;
    }

    @Override
    public boolean deleteUser(Long id) {
        return removeById(id);
    }

    @Override
    public boolean resetPassword(Long id) {
        User user = getById(id);
        if (user != null) {
            user.setPassword("123456"); // 重置为默认密码
            EntityUtils.setUpdateInfo(user);
            return updateById(user);
        }
        return false;
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        User user = getById(id);
        if (user != null) {
            user.setStatus(status);
            EntityUtils.setUpdateInfo(user);
            return updateById(user);
        }
        return false;
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        // TODO: 实现从用户角色关联表获取角色ID列表
        // 这里需要根据实际的数据库设计来实现
        // 暂时返回空列表
        return new ArrayList<>();
    }

    @Override
    public boolean assignUserRoles(Long userId, List<Long> roleIds) {
        // TODO: 实现分配用户角色的逻辑
        // 这里需要根据实际的数据库设计来实现
        // 暂时返回true
        return true;
    }
} 