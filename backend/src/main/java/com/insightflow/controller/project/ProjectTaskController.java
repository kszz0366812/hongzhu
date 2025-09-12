package com.insightflow.controller.project;

import com.github.pagehelper.PageInfo;
import com.insightflow.dto.ProjectTaskDTO;
import com.insightflow.entity.project.ProjectTask;
import com.insightflow.service.project.ProjectTaskService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "项目任务管理")
@RestController
@RequestMapping("/project-task")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Operation(summary = "分页查询项目任务列表")
    @GetMapping("/list")
    public Result<PageInfo<ProjectTaskDTO>> getTaskList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "任务类型") @RequestParam(required = false) String type,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "指派者ID") @RequestParam(required = false) String assignerId,
            @Parameter(description = "负责人ID") @RequestParam(required = false) String assigneeId,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId) {
        PageInfo<ProjectTaskDTO> taskList = projectTaskService.getTaskListWithNames(page, size, type, status, assignerId, assigneeId, projectId);
        return Result.success(taskList);
    }

    @Operation(summary = "获取项目任务列表（不分页）")
    @GetMapping("/all")
    public Result<List<ProjectTaskDTO>> getAllTaskList(
            @Parameter(description = "任务类型") @RequestParam(required = false) String type,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "指派者ID") @RequestParam(required = false) String assignerId,
            @Parameter(description = "负责人ID") @RequestParam(required = false) String assigneeId,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId) {
        List<ProjectTaskDTO> taskList = projectTaskService.getTaskListWithNames(type, status, assignerId, assigneeId, projectId);
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
        boolean success = projectTaskService.removeById(id);
        return Result.success(success);
    }
} 