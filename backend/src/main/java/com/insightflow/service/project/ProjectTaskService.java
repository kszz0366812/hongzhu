package com.insightflow.service.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.dto.ProjectTaskDTO;
import com.insightflow.entity.project.ProjectTask;

public interface ProjectTaskService extends IService<ProjectTask> {

    /**
     * 分页查询项目任务列表
     */
    IPage<ProjectTask> getTaskList(Integer page, Integer size, String type, String status,String assignerId,String assigneeId, Long projectId);

    /**
     * 分页查询项目任务列表（包含责任人名称和项目名称）
     */
    IPage<ProjectTaskDTO> getTaskListWithNames(Integer page, Integer size, String type, String status,String assignerId,String assigneeId,Long projectId);

    /**
     * 创建项目任务
     */
    Long createTask(ProjectTaskDTO taskDTO);

    /**
     * 更新项目任务
     */
    boolean updateTask(ProjectTaskDTO taskDTO);

    /**
     * 删除项目任务
     */
    boolean deleteTask(Long id);

    /**
     * 获取项目任务统计信息
     */
    Object getTaskStatistics();
} 