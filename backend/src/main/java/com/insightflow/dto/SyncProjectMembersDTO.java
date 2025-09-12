package com.insightflow.dto;

import lombok.Data;

import java.util.List;

/**
 * 同步项目成员DTO
 */
@Data
public class SyncProjectMembersDTO {
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 成员信息列表
     */
    private List<MemberInfo> members;
    
    /**
     * 成员信息
     */
    @Data
    public static class MemberInfo {
        /**
         * 员工ID
         */
        private Long employeeId;
        
        /**
         * 角色(MANAGER,MEMBER等)
         */
        private String role;
    }
}