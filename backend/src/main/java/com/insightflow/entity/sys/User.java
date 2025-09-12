package com.insightflow.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.insightflow.entity.BaseEntity;
import com.insightflow.entity.employee.EmployeeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 关联员工ID
     */
    private Long employeeId;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 微信头像
     */
    private String avatarUrl;

    /**
     * 性别(0:未知,1:男,2:女)
     */
    private Integer gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 是否已绑定员工(0:未绑定,1:已绑定)
     */
    private Integer isBound;

    /**
     * 员工信息（非数据库字段）
     */
    @TableField(exist = false)
    private EmployeeInfo employeeInfo;

    /**
     * 记住密码（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean rememberMe;
} 