package com.insightflow.entity.task;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_target")
public class TaskTarget extends BaseEntity {

    /**
     * 目标类型（1-销售额 2-拜访量）
     */
    private Integer targetType;

    /**
     * 目标时间（年月）
     */
    private LocalDate targetDate;

    /**
     * 目标人员ID
     */
    private Long targetPersonId;

    /**
     * 目标人员姓名
     */
    private String targetPersonName;

    /**
     * 目标终端ID
     */
    private Long targetTerminalId;

    /**
     * 目标终端名称
     */
    private String targetTerminalName;

    /**
     * 目标值
     */
    private BigDecimal targetValue;

    /**
     * 完成值
     */
    private BigDecimal completedValue;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 状态（0-未完成 1-已完成）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    @TableField("executor_id")
    private Long executorId;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("task_name")
    private String taskName;
    
    @TableField("target_amount")
    private BigDecimal targetAmount;
    
    @TableField("achieved_amount")
    private BigDecimal achievedAmount;
    
    @TableField("task_type")
    private String taskType;
    
    @TableField("task_status")
    private String taskStatus;
    
    @TableField("task_priority")
    private String taskPriority;
    
    @TableField("task_description")
    private String taskDescription;
    
    @TableField("evaluation_score")
    private BigDecimal evaluationScore;
    
    @TableField("evaluation_comment")
    private String evaluationComment;

} 