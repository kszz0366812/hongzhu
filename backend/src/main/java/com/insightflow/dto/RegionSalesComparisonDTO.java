package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegionSalesComparisonDTO {
    
    /**
     * 区域名称
     */
    private String regionName;
    
    /**
     * 销售额
     */
    private BigDecimal salesAmount;
    
    /**
     * 同比增长率
     */
    private BigDecimal yearOnYear;
    
    /**
     * 环比增长率
     */
    private BigDecimal monthOnMonth;
} 