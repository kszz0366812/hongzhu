package com.insightflow.controller.template;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.common.Result;
import com.insightflow.dto.ItfTemplateDTO;
import com.insightflow.entity.template.ItfTemplate;
import com.insightflow.service.template.ItfTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.github.pagehelper.PageInfo;

@Tag(name = "接口模板管理", description = "接口模板的增删改查接口")
@RestController
@RequestMapping("/itfTemplate")
public class ItfTemplateController {

    @Autowired
    private ItfTemplateService itfTemplateService;

    @Operation(summary = "创建接口模板", description = "创建一个新的接口模板")
    @RequestMapping("/create")
    public Result<Void> createTemplate(@Parameter(description = "模板信息") @Validated @RequestBody ItfTemplateDTO dto) {
        itfTemplateService.createTemplate(dto);
        return Result.success();
    }

    @Operation(summary = "更新接口模板", description = "更新已存在的接口模板")
    @RequestMapping("/update")
    public Result<Void> updateTemplate(@Parameter(description = "模板信息") @Validated @RequestBody ItfTemplateDTO dto) {
        itfTemplateService.updateTemplate(dto);
        return Result.success();
    }

    @Operation(summary = "删除接口模板", description = "根据ID删除接口模板")
    @RequestMapping("/delete/{id}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable Integer id) {
        itfTemplateService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "分页查询接口模板")
    @RequestMapping("/page")
    public Result<PageInfo<ItfTemplate>> pageTemplate(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "模板名称") @RequestParam(required = false) String tepName) {
        return Result.success(itfTemplateService.pageTemplate(current, size, tepName));
    }

    @Operation(summary = "获取接口模板", description = "根据名称接口列表")
    @RequestMapping("/getlist")
    public Result<List<ItfTemplate>> getlist(
            @RequestBody ItfTemplateDTO dto) {
        return Result.success(itfTemplateService.getTemplateList(dto.getTepName()));
    }
} 