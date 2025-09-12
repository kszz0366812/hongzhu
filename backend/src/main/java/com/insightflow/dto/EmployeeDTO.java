package com.insightflow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class EmployeeDTO {
    private Long id;
    
    @NotBlank(message = "工号不能为空")
    private String employeeCode;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    @NotBlank(message = "所属大区不能为空")
    private String regionLevel1;
    
    @NotBlank(message = "所属地市不能为空")
    private String regionLevel2;
    
    @NotBlank(message = "所属区域不能为空")
    private String regionLevel3;
    
    private String responsibleRegions;
    private Long directLeaderId;
    
    @NotBlank(message = "岗位名称不能为空")
    private String position;
    
    @NotBlank(message = "职级不能为空")
    private String rank;
    
    @NotBlank(message = "渠道不能为空")
    private String channel;
    
    @NotNull(message = "入司日期不能为空")
    private LocalDate joinDate;

    private String avatar;
} 