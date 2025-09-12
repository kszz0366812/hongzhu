package com.insightflow.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegionSalesVO {
    private String regionName;
    private String productName;
    private String timePoint;
    private BigDecimal salesAmount;
    private BigDecimal percentage;
    private BigDecimal yearOnYear; // 同比增长率
    private BigDecimal monthOnMonth; // 环比增长率
} 