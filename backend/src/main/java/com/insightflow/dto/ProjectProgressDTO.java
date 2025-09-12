package com.insightflow.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectProgressDTO {

    /**
     * 进度ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 进度百分比(0-100)
     */
    private Integer progressPercentage;

    /**
     * 进度内容描述
     */
    private String progressContent;

    /**
     * 更新前进度
     */
    private Integer previousProgress;

    /**
     * 进度变化值
     */
    private Integer progressChange;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 项目名称（用于显示）
     */
    private String projectName;

    /**
     * 创建人姓名（用于显示）
     */
    private String creatorName;
} 