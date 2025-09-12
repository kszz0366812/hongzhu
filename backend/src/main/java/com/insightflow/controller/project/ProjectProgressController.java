package com.insightflow.controller.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insightflow.dto.ProjectProgressDTO;
import com.insightflow.service.project.ProjectProgressService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "项目进度管理")
@RestController
@RequestMapping("/project/progress")
public class ProjectProgressController {

    @Autowired
    private ProjectProgressService projectProgressService;

    @Operation(summary = "获取项目进度列表")
    @RequestMapping("/list")
    public Result<IPage<ProjectProgressDTO>> getProjectProgressList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long creatorId) {
        IPage<ProjectProgressDTO> progressList = projectProgressService.getProjectProgressList(page, size, projectId, creatorId);
        return Result.success(progressList);
    }

    @Operation(summary = "获取项目进度历史")
    @RequestMapping("/history/{projectId}")
    public Result<List<ProjectProgressDTO>> getProjectProgressHistory(@PathVariable Long projectId) {
        List<ProjectProgressDTO> history = projectProgressService.getProjectProgressHistory(projectId);
        return Result.success(history);
    }

    @Operation(summary = "获取最新项目进度")
    @RequestMapping("/latest/{projectId}")
    public Result<ProjectProgressDTO> getLatestProjectProgress(@PathVariable Long projectId) {
        ProjectProgressDTO latestProgress = projectProgressService.getLatestProjectProgress(projectId);
        return Result.success(latestProgress);
    }

    @Operation(summary = "创建项目进度")
    @RequestMapping("/create")
    public Result<Long> createProjectProgress(@RequestBody ProjectProgressDTO projectProgressDTO) {
        Long progressId = projectProgressService.createProjectProgress(projectProgressDTO);
        return Result.success(progressId);
    }

    @Operation(summary = "更新项目进度")
    @RequestMapping("/update")
    public Result<Boolean> updateProjectProgress(@RequestBody ProjectProgressDTO projectProgressDTO) {
        boolean success = projectProgressService.updateProjectProgress(projectProgressDTO);
        return Result.success(success);
    }

    @Operation(summary = "删除项目进度")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> deleteProjectProgress(@PathVariable Long id) {
        boolean success = projectProgressService.removeById(id);
        return Result.success(success);
    }

    @Operation(summary = "获取项目进度统计信息")
    @RequestMapping("/statistics")
    public Result<Object> getProjectProgressStatistics(@RequestParam(required = false) Long projectId) {
        Object statistics = projectProgressService.getProjectProgressStatistics(projectId);
        return Result.success(statistics);
    }
} 