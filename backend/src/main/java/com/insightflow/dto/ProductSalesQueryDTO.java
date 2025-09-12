package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductSalesQueryDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private String series;
    private String regionCode;
    private String customerManager;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeUnit;  // 时间单位：DAY, WEEK, MONTH
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式：ASC, DESC
} 