package com.insightflow.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaskTargetVO {
    private Long id;
    private Long executorId;
    private String executorName;
    private String executorCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String taskName;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private BigDecimal completionRate;  // 完成率
    private Integer status;             // 状态：0-进行中，1-已完成，2-已超时
    private String remark;
} 