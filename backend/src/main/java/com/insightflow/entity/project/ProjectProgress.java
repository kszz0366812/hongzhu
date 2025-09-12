package com.insightflow.entity.project;

import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_project_progress")
public class ProjectProgress extends BaseEntity {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 创建人ID
     */
    private Long creatorId;

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
} 