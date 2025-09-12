package com.insightflow.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.insightflow.entity.template.ItfTemplate;
import com.insightflow.service.common.CustomizeService;
import com.insightflow.service.template.ItfTemplateService;
import com.insightflow.common.util.Result;
import com.insightflow.common.util.SqlConvert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;

/**
 * @Author: sy
 * @CreateTime: 2025-06-11
 * @Description: 通用接口
 * @Version: 1.0
 */
@RestController
@Slf4j
@Tag(name = "自定义接口")
@RequestMapping("/customize")
public class CustomizeController {

    @Autowired
    private ItfTemplateService  itfTemplateService;
    @Autowired
    private CustomizeService customService;
    /**
     * 自定义接口信息获取
     * @return
     */
    @Operation(summary = "自定义接口信息获取", tags = {})
    @RequestMapping("/getInterfaceInfo/{id}")
    public Result<Object> getInterfaceInfo(@PathVariable Integer id,  @RequestBody JSONObject param ) {
        ItfTemplate template = itfTemplateService.getById(id);
        if (template == null) {
            return Result.error("接口不存在");
        }
        String resourceSql = template.getResourceSql();
        String sql  = SqlConvert.convert(resourceSql, param);
        try{
            List<Map<String, Object>> data = customService.getCustomizeSql(sql);
            return Result.success(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("接口异常,请检查sql");
        }

    }


    /**
     * 自定义接口信息分页获取
     * @return
     */
    @RequestMapping("/page/{id}/{current}/{size}")
    public Result<PageInfo<Object>> getInterfaceInfoPage(@PathVariable Integer id, @PathVariable Integer current, @PathVariable Integer size, @RequestBody JSONObject param) {
        ItfTemplate template = itfTemplateService.getById(id);
        if (template == null) {
            return Result.error("接口不存在");
        }
        String resourceSql = template.getResourceSql();
        String sql  = SqlConvert.convert(resourceSql, param);
        PageInfo<Object> result = customService.getCustomizeSqlPage(sql, current, size);
        return Result.success(result);
    }

    /**
     * 获取参数
     */
    @Operation(summary = "获取自定义接口参数", tags = {})
    @RequestMapping("/getInterfaceParam/{id}")
    public Result<Object> getInterfaceParam(@PathVariable Integer id) {
        ItfTemplate template = itfTemplateService.getById(id);
        if (template == null) {
            return Result.error("接口不存在");
        }
        String resourceSql = template.getResourceSql();
        List<String> params = SqlConvert.parseParams(resourceSql);
        return Result.success(params);
    }

    /**
     * 获取自定义接口列名
     */
    @Operation(summary = "获取自定义接口列名", tags = {})
    @RequestMapping("/getInterfaceColumn/{id}")
    public Result<Object> getInterfaceColumn(@PathVariable Integer id) {
        ItfTemplate template = itfTemplateService.getById(id);
        if (template == null) {
            return Result.error("接口不存在");
        }
        String resourceSql = template.getResourceSql();
        List<String> columns = SqlConvert.parseColumns(resourceSql);
        return Result.success(columns);
    }
}
