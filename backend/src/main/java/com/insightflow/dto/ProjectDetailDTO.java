package com.insightflow.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目详情DTO
 */
@Data
public class ProjectDetailDTO {

    /**
     * 项目基本信息
     */
    private ProjectInfo projectInfo;

    /**
     * 项目进度节点列表
     */
    private List<ProgressNode> progressNodes;

    /**
     * 项目成员列表
     */
    private List<ProjectMember> projectMembers;

    /**
     * 项目任务列表
     */
    private List<ProjectTask> projectTasks;

    /**
     * 项目信息
     */
    @Data
    public static class ProjectInfo {
        private Long id;
        private String name;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long managerId;
        private String managerName;
        private String createName;
        private String priority;
        private Integer topUp;
        private String level;
        private String status;
        private Integer progress;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    /**
     * 进度节点信息
     */
    @Data
    public static class ProgressNode {
        private Long id;
        private Long projectId;
        private Long creatorId;
        private String creatorName;
        private Integer progressPercentage;
        private String progressContent;
        private Integer previousProgress;
        private Integer progressChange;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    /**
     * 项目成员信息
     */
    @Data
    public static class ProjectMember {
        private Long id;
        private Long projectId;
        private Long employeeId;
        private String employeeName;
        private String role;
        private String avatar;
        private LocalDateTime joinTime;
        private LocalDateTime createTime;
    }

    /**
     * 项目任务信息
     */
    @Data
    public static class ProjectTask {
        private Long id;
        private String title;
        private String description;
        private Long projectId;
        private Long assigneeId;
        private String assigneeName;
        private Long assignerId;
        private String assignerName;
        private String priority;
        private String status;
        private LocalDateTime deadline;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
} 