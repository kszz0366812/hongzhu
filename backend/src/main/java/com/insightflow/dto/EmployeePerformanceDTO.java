package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EmployeePerformanceDTO {
    private String employeeId;
    private String employeeName;
    private LocalDateTime timePoint;
    private Integer visitCount;
    private Integer dealCount;
    private BigDecimal totalSales;
    private Double conversionRate;
    private BigDecimal averageOrderValue;
} 