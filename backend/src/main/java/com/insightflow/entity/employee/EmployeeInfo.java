package com.insightflow.entity.employee;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee_info")
public class EmployeeInfo extends BaseEntity {
    
    @TableField("employee_code")
    private String employeeCode;
    
    @TableField("name")
    private String name;

    @TableField("avatar")
    private String avatar;

    @TableField("status")
    private Integer status;
    
    @TableField("region_level1")
    private String regionLevel1;
    
    @TableField("region_level2")
    private String regionLevel2;
    
    @TableField("region_level3")
    private String regionLevel3;
    
    @TableField("responsible_regions")
    private String responsibleRegions;
    
    @TableField("direct_leader_id")
    private Long directLeaderId;

    @TableField("direct_leader")
    private String directLeader;
    
    @TableField("position")
    private String position;
    
    @TableField("levels")
    private String levels;
    
    @TableField("channel")
    private String channel;
    
    @TableField("join_date")
    private LocalDate joinDate;

} 