package com.insightflow.entity.task;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.insightflow.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_target")
public class TaskTarget extends BaseEntity {

    /**
     * 执行人ID
     */
    @TableField("executor_id")
    private Long executorId;

    /**
     * 执行人
     */
    @TableField("executor")
    private String executor;

    /**
     * 任务开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务目标金额
     */
    @TableField("target_amount")
    private BigDecimal targetAmount;

    /**
     * 已达成金额
     */
    @TableField("achieved_amount")
    private BigDecimal achievedAmount;

} 