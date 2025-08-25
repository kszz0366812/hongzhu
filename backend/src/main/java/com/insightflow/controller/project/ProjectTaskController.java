package com.insightflow.controller.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insightflow.dto.ProjectTaskDTO;
import com.insightflow.entity.project.ProjectTask;
import com.insightflow.service.project.ProjectTaskService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "项目任务管理")
@RestController
    @RequestMapping("/project-task")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Operation(summary = "获取项目任务列表")
    @RequestMapping("/list")
    public Result<IPage<ProjectTaskDTO>> getTaskList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignerId,
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false) Long projectId) {
        IPage<ProjectTaskDTO> taskList = projectTaskService.getTaskListWithNames(page, size, type, status,assignerId,assigneeId, projectId);
        return Result.success(taskList);
    }

    @Operation(summary = "获取项目任务统计信息")
    @RequestMapping("/statistics")
    public Result<Object> getTaskStatistics() {
        Object statistics = projectTaskService.getTaskStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "创建项目任务")
    @RequestMapping("/create")
    public Result<Long> createTask(@RequestBody ProjectTaskDTO taskDTO) {
        Long taskId = projectTaskService.createTask(taskDTO);
        return Result.success(taskId);
    }

    @Operation(summary = "更新项目任务")
    @RequestMapping("/update")
    public Result<Boolean> updateTask(@RequestBody ProjectTaskDTO taskDTO) {
        boolean success = projectTaskService.updateTask(taskDTO);
        return Result.success(success);
    }

    @Operation(summary = "删除项目任务")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> deleteTask(@PathVariable Long id) {
        boolean success = projectTaskService.deleteTask(id);
        return Result.success(success);
    }
} 