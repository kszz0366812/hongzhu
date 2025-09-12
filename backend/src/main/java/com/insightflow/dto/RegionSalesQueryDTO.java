package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegionSalesQueryDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String regionName;
    private Integer regionLevel; // 1: 大区, 2: 地市, 3: 区域
    private String timeUnit; // day, week, month
}