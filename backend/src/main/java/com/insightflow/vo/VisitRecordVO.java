package com.insightflow.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitRecordVO {
    private Long id;
    private Long visitorId;
    private String visitorName;
    private String visitorCode;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;
    private Long terminalId;
    private String terminalName;
    private String terminalCode;
    private String terminalType;
    private LocalDateTime visitTime;
    private Integer isDeal;
    private String dealRemark;
    private String visitRemark;
    private String customerManager;
    private Integer visitStatus;  // 拜访状态：0-未开始，1-进行中，2-已完成，3-已取消
} 