package com.insightflow.dto;

import lombok.Data;

@Data
public class EmployeeVisitRateDTO {
    private String employeeId;
    private String employeeName;
    private Integer totalVisits;
    private Integer completedVisits;
    private Double completionRate;
} 