package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDTO {

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
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 项目经理ID
     */
    private Long managerId;

    /**
     * 优先级(LOW,MEDIUM,HIGH)
     */
    private String priority;

    /**
     * 项目级别(URGENT,IMPORTANT,NORMAL,LONG_TERM)
     */
    private String level;

    /**
     * 状态(PENDING,ONGOING,COMPLETED,OVERDUE)
     */
    private String status;

    /**
     * 进度(0-100)
     */
    private Integer progress;

     /**
     * 是否置顶显示
     */
     private Integer topUp;

    /**
     * 邀请的员工ID列表
     */
    private List<Long> invitedMembers;
} 