package com.insightflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class TerminalSyncDTO {
    private List<TerminalData> data;
    private Long total;
    
    @Data
    public static class TerminalData {
        private String terminalCode;
        private String terminalName;
        private String terminalType;
        private List<String> tags;
        private String customerManager;
        private Integer isScheduled;
    }
} 