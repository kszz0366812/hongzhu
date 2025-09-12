package com.insightflow.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class DealSyncDTO {
    private List<DealData> data;
    private Long total;
    
    @Data
    public static class DealData {
        private String dealTime;
        private Long visitId;
        private Long dealEmployeeId;
        private Long productId;
        private Integer quantity;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
} 