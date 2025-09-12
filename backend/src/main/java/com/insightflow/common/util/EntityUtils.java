package com.insightflow.common.util;

import com.insightflow.entity.BaseEntity;
import com.insightflow.vo.LoginInfoVO;

import java.time.LocalDateTime;

/**
 * 实体工具类，用于自动设置实体的通用字段
 */
public class EntityUtils {

    /**
     * 设置实体的创建人ID和时间
     */
    public static void setCreateInfo(BaseEntity entity) {
        try {
            LoginInfoVO loginInfo = JwtUtils.getLoginInfo();
            if (loginInfo != null && loginInfo.getEmployeeId() != null) {
                entity.setCreatorId(loginInfo.getEmployeeId());
            }
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
        } catch (Exception e) {
            // 如果获取登录信息失败，只设置时间
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
        }
    }

    /**
     * 设置实体的更新时间
     */
    public static void setUpdateInfo(BaseEntity entity) {
        entity.setUpdateTime(LocalDateTime.now());
    }
} 