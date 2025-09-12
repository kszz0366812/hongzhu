package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegionProductSalesDTO {
    
    /**
     * 商品编码
     */
    private String productCode;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 销售数量
     */
    private Integer salesQuantity;
    
    /**
     * 销售额
     */
    private BigDecimal salesAmount;
    
    /**
     * 销售日期
     */
    private String salesDate;
    
    /**
     * 销售次数
     */
    private Integer salesCount;
} 