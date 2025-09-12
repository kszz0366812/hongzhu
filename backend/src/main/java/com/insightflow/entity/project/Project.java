package com.insightflow.entity.project;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_project")
public class Project extends BaseEntity {

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
     * 是否置顶
     */
    private Integer topUp ;

    /**
     * 置顶时间
     */
    private LocalDateTime topUpTime;
} 