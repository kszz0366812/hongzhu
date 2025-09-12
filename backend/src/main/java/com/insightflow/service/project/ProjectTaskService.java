package com.insightflow.service.project;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.dto.ProjectTaskDTO;
import com.insightflow.entity.project.ProjectTask;

import java.util.List;

public interface ProjectTaskService extends IService<ProjectTask> {

    /**
     * 分页查询任务列表
     */
    PageInfo<ProjectTask> getTaskList(Integer page, Integer size, String type, String status, String assignerId, String assigneeId, Long projectId);

    /**
     * 分页查询任务列表（包含名称信息）
     */
    PageInfo<ProjectTaskDTO> getTaskListWithNames(Integer page, Integer size, String type, String status, String assignerId, String assigneeId, Long projectId);

    /**
     * 获取任务列表（不分页）
     */
    List<ProjectTask> getTaskList(String type, String status, String assignerId, String assigneeId, Long projectId);

    /**
     * 获取任务列表（包含名称信息，不分页）
     */
    List<ProjectTaskDTO> getTaskListWithNames(String type, String status, String assignerId, String assigneeId, Long projectId);

    /**
     * 创建项目任务
     */
    Long createTask(ProjectTaskDTO taskDTO);

    /**
     * 更新项目任务
     */
    boolean updateTask(ProjectTaskDTO taskDTO);


    /**
     * 获取项目任务统计信息
     */
    Object getTaskStatistics();
}