package com.insightflow.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.sys.User;
import com.insightflow.vo.LoginInfoVO;

import java.util.List;

public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);
    
    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(String username, String oldPassword, String newPassword);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 获取用户信息
     * @return 用户信息
     */
    LoginInfoVO generateLoginInfo(User user);

    /**
     * 分页查询用户列表
     */
    PageInfo<User> getUserPage(Integer current, Integer size, String username, String realName, String mobile, String email, Integer status);

    /**
     * 获取用户列表（不分页）
     */
    List<User> getUserList(String username, String realName, String mobile, String email, Integer status);

    /**
     * 新增用户
     */
    User addUser(User user);

    /**
     * 更新用户
     */
    String updateUser(User user);



    /**
     * 重置用户密码
     */
    boolean resetPassword(Long id);

    /**
     * 修改用户状态
     */
    boolean changeStatus(Long id, Integer status);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);
    
    /**
     * 批量保存用户信息
     * @param userList 用户列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<User> userList);
} 