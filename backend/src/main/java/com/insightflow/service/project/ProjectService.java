package com.insightflow.service.project;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.dto.ProjectDTO;
import com.insightflow.dto.ProjectDetailDTO;
import com.insightflow.dto.ProjectListDTO;
import com.insightflow.entity.project.Project;
import com.insightflow.entity.project.ProjectMember;

import java.util.List;



public interface ProjectService extends IService<Project> {

    /**
     * 获取项目列表
     */
    List<Project> getProjectList(String status, String keyword);

    /**
     * 分页查询项目列表
     */
    PageInfo<Project> getProjectList(Integer current, Integer size, String status, String keyword);

    /**
     * 分页查询项目列表（包含成员信息）
     */
    PageInfo<ProjectListDTO> getProjectListWithMembers(Integer current, Integer size, String status, String keyword);

    /**
     * 获取项目列表（包含成员信息，不分页）
     */
    List<ProjectListDTO> getProjectListWithMembers(String status, String keyword);

    /**
     * 创建项目
     */
    Long createProject(ProjectDTO projectDTO);

    /**
     * 更新项目
     */
    boolean updateProject(ProjectDTO projectDTO);



    /**
     * 获取项目统计信息
     */
    Object getProjectStatistics();

    /**
     * 获取项目详情（包含项目信息和进度节点信息）
     */
    ProjectDetailDTO getProjectDetail(Long projectId);

    /**
     * 同步项目成员（先删除原有成员，再添加新成员）
     */
    boolean syncProjectMembers(Long projectId, List<ProjectMember> members);
    
    /**
     * 批量保存项目信息
     * @param projectList 项目列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<Project> projectList);
} 