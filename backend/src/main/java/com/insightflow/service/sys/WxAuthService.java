package com.insightflow.service.sys;

import com.insightflow.dto.WxLoginDTO;

public interface WxAuthService {

    /**
     * 微信小程序登录
     */
    Object wxLogin(WxLoginDTO wxLoginDTO);

    /**
     * 检查员工信息
     */
    Object checkEmployee(String employeeCode);

    /**
     * 绑定员工信息
     */
    Object bindEmployee(String openId, String employeeCode);
} 