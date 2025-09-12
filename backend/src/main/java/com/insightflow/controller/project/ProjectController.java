package com.insightflow.controller.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insightflow.dto.ProjectDTO;
import com.insightflow.dto.ProjectDetailDTO;
import com.insightflow.dto.ProjectListDTO;
import com.insightflow.entity.project.Project;
import com.insightflow.entity.project.ProjectMember;

import java.util.List;
import com.insightflow.service.project.ProjectService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "项目管理")
@RestController
    @RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "获取项目列表(不分页)")
    @RequestMapping("/getlist")
    public Result<List<Project>> getProjectList(
            @RequestParam(value = "status", required = false)String status,
            @RequestParam(value = "keyword", required = false)String  keyword
    ) {
        List<Project> projectList = projectService.getProjectList(status, keyword);
        return Result.success(projectList);
    }



    @Operation(summary = "分页查询项目列表")
    @RequestMapping("/page")
    public Result<PageInfo<ProjectListDTO>> getProjectList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageInfo<ProjectListDTO> projectList = projectService.getProjectListWithMembers(current, size, status, keyword);
        return Result.success(projectList);
    }

    @Operation(summary = "获取项目列表（不分页）")
    @GetMapping("/all")
    public Result<List<ProjectListDTO>> getAllProjectList(
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        List<ProjectListDTO> projectList = projectService.getProjectListWithMembers(status, keyword);
        return Result.success(projectList);
    }

    @Operation(summary = "获取项目统计信息")
    @RequestMapping("/statistics")
    public Result<Object> getProjectStatistics() {
        Object statistics = projectService.getProjectStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "获取项目详情")
    @RequestMapping("/detail/{projectId}")
    public Result<ProjectDetailDTO> getProjectDetail(@PathVariable Long projectId) {
        ProjectDetailDTO projectDetail = projectService.getProjectDetail(projectId);
        if (projectDetail == null) {
            return Result.error("项目不存在");
        }
        return Result.success(projectDetail);
    }

    @Operation(summary = "创建项目")
    @RequestMapping("/create")
    public Result<Long> createProject(@RequestBody ProjectDTO projectDTO) {
        Long projectId = projectService.createProject(projectDTO);
        return Result.success(projectId);
    }

    @Operation(summary = "更新项目")
    @RequestMapping("/update")
    public Result<Boolean> updateProject(@RequestBody ProjectDTO projectDTO) {
        boolean success = projectService.updateProject(projectDTO);
        return Result.success(success);
    }

    @Operation(summary = "删除项目")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> deleteProject(@PathVariable Long id) {
        boolean success = projectService.removeById(id);
        return Result.success(success);
    }

    @Operation(summary = "同步项目成员")
    @PostMapping("/sync-members/{projectId}")
    public Result<Boolean> syncProjectMembers(@PathVariable Long projectId, @RequestBody List<ProjectMember> members) {
        boolean success = projectService.syncProjectMembers(projectId, members);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("项目成员同步失败");
        }
    }
} 