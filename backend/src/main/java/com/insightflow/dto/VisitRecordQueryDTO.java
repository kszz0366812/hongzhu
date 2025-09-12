package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitRecordQueryDTO {
    private Long visitorId;
    private String visitorName;
    private String visitorCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Long terminalId;
    private String terminalName;
    private String terminalCode;
    private String terminalType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer isDeal;
    private Integer visitStatus;
    private String customerManager;
    private String timeUnit;  // 时间单位：DAY, WEEK, MONTH
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 