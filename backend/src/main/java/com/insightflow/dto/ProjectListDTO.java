package com.insightflow.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目列表DTO
 */
@Data
public class ProjectListDTO {
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目状态
     */
    private String status;
    
    /**
     * 优先级(LOW,MEDIUM,HIGH)
     */
    private String priority;
    
    /**
     * 项目级别(URGENT,IMPORTANT,NORMAL,LONG_TERM)
     */
    private String level;
    
    /**
     * 进度(0-100)
     */
    private Integer progress;
    
    /**
     * 责任人ID
     */
    private Long managerId;
    
    /**
     * 责任人姓名
     */
    private String managerName;
    
    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 是否置顶
     */
    private Integer topUp;
    
    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 项目成员列表
     */
    private List<ProjectMemberInfo> members;
    
    /**
     * 项目成员信息
     */
    @Data
    public static class ProjectMemberInfo {
        /**
         * 员工ID
         */
        private Long employeeId;
        
        /**
         * 员工姓名
         */
        private String employeeName;
        
        /**
         * 成员角色
         */
        private String role;
        
        /**
         * 加入时间
         */
        private LocalDateTime joinTime;

        /**
         * 头像
         */
        private String avatar;
    }
}