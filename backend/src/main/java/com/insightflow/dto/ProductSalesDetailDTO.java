package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSalesDetailDTO {
    private String productId;
    private String productName;
    private String category;
    private Integer totalSalesQuantity;
    private BigDecimal totalSalesAmount;
    private Integer customerCount;
    private BigDecimal averagePrice;
    private Double marketShare;
} 