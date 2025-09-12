package com.insightflow.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSalesVO {
    private Long productId;
    private String productCode;
    private String productName;
    private String specification;
    private String series;
    private BigDecimal unitPrice;
    private BigDecimal casePrice;
    private Integer salesCount;        // 销售数量
    private BigDecimal salesAmount;    // 销售金额
    private String regionCode;         // 区域编码
    private String regionName;         // 区域名称
    private String customerManager;    // 客户经理
    private LocalDateTime salesDate;   // 销售日期
} 