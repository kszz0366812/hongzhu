package com.insightflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class VisitSyncDTO {
    private List<VisitData> data;
    private Long total;
    
    @Data
    public static class VisitData {
        private Long visitorId;
        private String visitTime;
        private Long terminalId;
        private Integer isDeal;
    }
} 