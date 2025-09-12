package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaskTargetDTO {
    private Long id;
    private Long executorId;
    private String executor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String taskName;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private BigDecimal achievementRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 