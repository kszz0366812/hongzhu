package com.insightflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeSyncDTO {
    private List<EmployeeData> data;
    private Long total;
    
    @Data
    public static class EmployeeData {
        private String employeeCode;
        private String name;
        private Integer status;
        private String regionLevel1;
        private String regionLevel2;
        private String regionLevel3;
        private List<String> responsibleRegions;
        private Long directLeaderId;
        private String position;
        private String level;
        private String channel;
        private String joinDate;
    }
}