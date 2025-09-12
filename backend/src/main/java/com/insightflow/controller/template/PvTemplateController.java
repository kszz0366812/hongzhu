package com.insightflow.controller.template;

import com.github.pagehelper.PageInfo;
import com.insightflow.common.Result;
import com.insightflow.dto.PvTemplateDTO;
import com.insightflow.entity.template.PvTemplate;
import com.insightflow.service.template.PvTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报表模板管理", description = "可视化报表模板的增删改查接口")
@RestController
@RequestMapping("/pvTemplate")
public class PvTemplateController {

    @Autowired
    private PvTemplateService pvTemplateService;

    @Operation(summary = "创建报表模板")
    @RequestMapping("/create")
    public Result<Void> create(@Parameter(description = "模板信息") @Validated @RequestBody PvTemplateDTO dto) {
        pvTemplateService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更新报表模板")
    @RequestMapping("/update")
    public Result<Void> update(@Parameter(description = "模板信息") @Validated @RequestBody PvTemplateDTO dto) {
        pvTemplateService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除报表模板")
    @RequestMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "模板ID") @PathVariable Long id) {
        pvTemplateService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "分页查询报表模板")
    @RequestMapping("/page")
    public Result<PageInfo<PvTemplate>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "模板名称") @RequestParam(required = false) String tepName) {
        return Result.success(pvTemplateService.page(current, size, tepName));
    }
} 