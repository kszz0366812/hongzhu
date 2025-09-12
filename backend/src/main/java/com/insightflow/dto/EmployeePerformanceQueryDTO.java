package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeePerformanceQueryDTO {
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeUnit;  // 时间单位：DAY, WEEK, MONTH
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 