package com.insightflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSalesTrendDTO {
    private String productId;
    private String productName;
    private LocalDateTime timePoint;
    private Integer salesQuantity;
    private BigDecimal salesAmount;
    private Integer customerCount;
} 