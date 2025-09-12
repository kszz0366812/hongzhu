package com.insightflow.service.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.ProjectProgressDTO;
import com.insightflow.entity.project.ProjectProgress;

import java.util.List;

public interface ProjectProgressService extends IService<ProjectProgress> {

    /**
     * 分页查询项目进度列表
     */
    IPage<ProjectProgressDTO> getProjectProgressList(Integer page, Integer size, Long projectId, Long creatorId);

    /**
     * 根据项目ID查询进度历史
     */
    List<ProjectProgressDTO> getProjectProgressHistory(Long projectId);

    /**
     * 创建项目进度
     */
    Long createProjectProgress(ProjectProgressDTO projectProgressDTO);

    /**
     * 更新项目进度
     */
    boolean updateProjectProgress(ProjectProgressDTO projectProgressDTO);


    /**
     * 获取项目进度统计信息
     */
    Object getProjectProgressStatistics(Long projectId);

    /**
     * 获取最新项目进度
     */
    ProjectProgressDTO getLatestProjectProgress(Long projectId);
}