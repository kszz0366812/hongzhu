package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskTargetQueryDTO {
    private Long executorId;
    private String executorName;
    private String executorCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String taskName;
    private Integer status;
    private String timeUnit;  // 时间单位：DAY, WEEK, MONTH
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 