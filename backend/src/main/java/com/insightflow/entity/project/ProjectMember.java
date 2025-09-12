package com.insightflow.entity.project;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_project_member")
public class ProjectMember extends BaseEntity {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * 角色
     */
    private String role;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;
} 