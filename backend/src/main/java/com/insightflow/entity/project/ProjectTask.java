package com.insightflow.entity.project;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_project_task")
public class ProjectTask extends BaseEntity {

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
} 