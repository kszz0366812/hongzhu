package com.insightflow.mapper.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.project.ProjectMember;

import io.lettuce.core.dynamic.annotation.Param;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
	 /**
     * 根据项目ID硬删除项目成员
     */
    @Delete("DELETE FROM sys_project_member WHERE project_id = #{projectId}")
    int deleteByProjectId(@Param("projectId") Long projectId);
} 