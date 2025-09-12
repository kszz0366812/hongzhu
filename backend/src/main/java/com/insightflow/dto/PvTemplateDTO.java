package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PvTemplateDTO {
    private Long id;
    private String tepName;
    private Long itfId;
    private Integer isPage;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 