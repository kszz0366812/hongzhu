package com.insightflow.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_wx_login_log")
public class WxLoginLog extends com.insightflow.entity.BaseEntity {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信session_key
     */
    private String sessionKey;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 状态(0:失败,1:成功)
     */
    private Integer status;
} 