package com.insightflow.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DealRecordVO {
    private Long id;
    private String salesOrderNo;
    private LocalDate salesDate;
    private String customerName;
    private String distributor;
    private String distributorType;
    private String customerManager;
    private String salesperson;
    private String productName;
    private String specification;
    private BigDecimal salesQuantity;
    private String customerCode;
    private String customerType;
    private Integer isGift;
    private String salesUnit;
    private String salespersonSupervisor;
    private String customerCategory;
    private String conversionUnit;
    private String productSeries;
    private String customerManagerName;
} 