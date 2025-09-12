package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EmployeeSalesRankDTO {
    private String employeeId;
    private String employeeName;
    private Integer rank;
    private BigDecimal totalSales;
    private Integer visitCount;
    private Integer dealCount;
    private Double conversionRate;
} 