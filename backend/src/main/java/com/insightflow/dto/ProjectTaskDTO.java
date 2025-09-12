package com.insightflow.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectTaskDTO {

    /**
     * 项目任务ID
     */
    private Long id;

    /**
     * 项目任务标题
     */
    private String title;

    /**
     * 项目任务描述
     */
    private String description;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 指派者ID
     */
    private Long assignerId;

    /**
     * 优先级(LOW,MEDIUM,HIGH)
     */
    private String priority;

    /**
     * 状态(PENDING,ONGOING,COMPLETED,OVERDUE)
     */
    private String status;

    /**
     * 截止时间
     */
    private LocalDate deadline;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 负责人姓名
     */
    private String assigneeName;

    /**
     * 指派者姓名
     */
    private String assignerName;

    /**
     * 创建人姓名
     */
    private String creatorName;
} 