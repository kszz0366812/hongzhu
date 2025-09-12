package com.insightflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class WholesalerSyncDTO {
    private List<WholesalerData> data;
    private Long total;
    
    @Data
    public static class WholesalerData {
        private String dealerCode;
        private String dealerName;
        private String level;
        private String contactPerson;
        private String contactPhone;
        private String customerManager;
    }
} 