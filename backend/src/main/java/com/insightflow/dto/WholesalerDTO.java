package com.insightflow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class WholesalerDTO {
    private Long id;
    
    @NotBlank(message = "经销商编码不能为空")
    private String dealerCode;
    
    @NotBlank(message = "经销商名称不能为空")
    private String dealerName;
    
    @NotBlank(message = "等级不能为空")
    private String level;
    
    private String contactPerson;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;
    
    @NotBlank(message = "客户经理不能为空")
    private String customerManager;
} 