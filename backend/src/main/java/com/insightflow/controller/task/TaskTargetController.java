package com.insightflow.controller.task;

import com.insightflow.common.Result;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.dto.TaskTargetDTO;
import com.insightflow.service.task.TaskTargetService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "任务目标管理")
@RestController
@RequestMapping("/task-target")
@RequiredArgsConstructor
public class TaskTargetController {

    private final TaskTargetService taskTargetService;

    @Operation(summary = "分页查询任务目标列表")
    @RequestMapping("/page")
    public Result<PageInfo<TaskTarget>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词（支持执行人、任务名称等字段的模糊搜索）") @RequestParam(required = false) String keyword) {
        return Result.success(taskTargetService.getTaskTargetPage(page, size, keyword));
    }

    @Operation(summary = "获取任务目标列表")
    @RequestMapping("/list")
    public Result<List<TaskTargetDTO>> list(
            @RequestParam Long executorId,
            @RequestParam String taskName) {
        return Result.success(taskTargetService.getTaskTargetList(executorId,taskName));
    }

    @Operation(summary = "获取任务目标详情")
    @RequestMapping("getById/{id}")
    public Result<TaskTarget> getById(@PathVariable Long id) {
        return Result.success(taskTargetService.getById(id));
    }

    @Operation(summary = "新增任务目标")
    @RequestMapping("save")
    public Result<Boolean> save(@RequestBody TaskTarget taskTarget) {
        return Result.success(taskTargetService.save(taskTarget));
    }

    @Operation(summary = "修改任务目标")
    @RequestMapping("/update")
    public Result<TaskTarget> update(@RequestBody TaskTarget taskTarget) {
        return Result.success(taskTargetService.update(taskTarget));
    }

    @Operation(summary = "删除任务目标")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskTargetService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "更新任务目标达成金额")
    @RequestMapping("/achieved-amount/{id}")
    public Result<Void> updateAchievedAmount(
            @PathVariable Long id,
            @RequestParam BigDecimal achievedAmount) {
        taskTargetService.updateAchievedAmount(id, achievedAmount);
        return Result.success();
    }
} 