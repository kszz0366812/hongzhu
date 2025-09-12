package com.insightflow.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class ProductSyncDTO {
    private List<ProductData> data;
    private Long total;
    
    @Data
    public static class ProductData {
        private String productCode;
        private String productName;
        private String specification;
        private BigDecimal unitPrice;
        private BigDecimal casePrice;
        private String series;
    }
} 