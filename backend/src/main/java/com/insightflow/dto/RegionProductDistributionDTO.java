package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegionProductDistributionDTO {
    
    /**
     * 商品编码
     */
    private String productCode;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 销售占比
     */
    private BigDecimal percentage;
    
    /**
     * 销售额
     */
    private BigDecimal salesAmount;

}