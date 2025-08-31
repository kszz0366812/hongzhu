package com.insightflow.controller.template;

import com.insightflow.dto.DbTableDTO;
import com.insightflow.service.template.TemplateService;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: sy
 * @CreateTime: 2025-06-10
 * @Description: 模版定义控制器
 * @Version: 1.0
 */
@Tag(name = "可视化表格配置接口")
@RestController
@RequestMapping("admin/template")
public class TemplateDefController {

    @Autowired
    private TemplateService templateService;

    /**
     * 获取数据表列表
     * @return
     */
    @Operation(summary = "获取数据表")
    @RequestMapping("getTables")
    public Result<List<DbTableDTO>> getTables(@RequestBody DbTableDTO dbTable) {
        List<DbTableDTO> tables = templateService.getTableList(dbTable.getTableComment());
        return Result.success(tables);
    }

    /**
     * 获取字段
     * @return
     */
    @Operation(summary = "获取字段")
    @RequestMapping("getColumns")
    public Result<DbTableDTO> getColumns(@RequestBody DbTableDTO dbTable) {
        DbTableDTO table = templateService.getTableDetail(dbTable.getTableName());
        return Result.success(table);
    }
}
