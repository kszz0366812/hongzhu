package com.insightflow.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.insightflow.common.enums.ExcelImportType;
import com.insightflow.common.util.Result;
import com.insightflow.common.util.SqlConvert;
import com.insightflow.dto.excel.ExcelDTO;
import com.insightflow.entity.template.ItfTemplate;
import com.insightflow.service.common.CustomizeService;
import com.insightflow.service.common.ExcelService;
import com.insightflow.service.template.ItfTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出控制器
 * 提供统一的Excel处理API接口
 */
@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;
    
    @Autowired
    private ItfTemplateService itfTemplateService;
    
    @Autowired
    private CustomizeService customService;

    /**
     * 导入Excel数据
     * @param file Excel文件
     * @param importType 导入类型（如：员工导入、产品导入等）
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<ExcelService.ImportResult<?>> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("importType") String importType) {
        
        try {
            // 根据导入类型获取对应的枚举
            ExcelImportType type = ExcelImportType.getByDisplayName(importType);
            if (type == null) {
                return Result.error("不支持的导入类型: " + importType);
            }
            System.out.println("文件大小"+file.getSize());
            // 检查文件大小（支持百万级数据，限制100MB）
            long fileSizeMB = file.getSize() / (1024 * 1024);
            if (fileSizeMB > 100) {
                return Result.error(String.format("文件大小超过100MB限制，当前文件大小: %dMB。如需导入更大文件，请联系管理员调整限制。", fileSizeMB));
            }
            
            log.info("开始导入Excel文件，类型: {}，文件大小: {}KB，文件名: {}",
                importType,  file.getSize(), file.getOriginalFilename());
            
            // 执行导入
            ExcelService.ImportResult<?> result = excelService.importExcel(file, type.getDtoClass());
            
            if (result.isSuccess() && result.getSuccessCount() > 0) {
                return Result.success("导入成功", result);
            } else {
                return Result.error(result.getMessage());
            }
            
        } catch (Exception e) {
            log.error("导入Excel失败", e);
            return Result.error("导入失败,请检查数据格式");
        }
    }

    /**
     * 导出Excel数据
     * 通过自定义接口ID和参数查询数据，然后导出为Excel
     * @param response HTTP响应
     * @param interfaceId 自定义接口ID
     * @param importType 导入类型（如：员工导入、产品导入等）
     * @param param 查询参数（JSON格式）
     */
    @PostMapping("/export/{interfaceId}")
    public void exportExcel(
            HttpServletResponse response,
            @PathVariable Integer interfaceId,
            @RequestParam("importType") String importType,
            @RequestBody(required = false) JSONObject param) {
        
        try {
            // 根据导入类型获取对应的枚举
            ExcelImportType type = ExcelImportType.getByDisplayName(importType);
            if (type == null) {
                throw new RuntimeException("不支持的导出类型: " + importType);
            }
            
            // 获取接口模板
            ItfTemplate template = itfTemplateService.getById(interfaceId);
            if (template == null) {
                throw new RuntimeException("接口不存在");
            }
            
            // 转换SQL并查询数据
            String resourceSql = template.getResourceSql();
            String sql = SqlConvert.convert(resourceSql, param != null ? param : new JSONObject());
            List<Map<String, Object>> dataList = customService.getCustomizeSql(sql);
            
            log.info("开始导出Excel，接口ID: {}，类型: {}，查询到数据条数: {}", 
                interfaceId, importType, dataList.size());
            
            // 执行导出 - 使用Map数据导出方法
            excelService.exportExcel(response, dataList, (Class<ExcelDTO>) type.getDtoClass());
            
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 下载Excel模板
     * @param response HTTP响应
     * @param importType 导入类型（如：员工导入、产品导入等）
     */
    @GetMapping("/template")
    public void downloadTemplate(
            HttpServletResponse response,
            @RequestParam("importType") String importType) {
        
        try {
            // 根据导入类型获取对应的枚举
            ExcelImportType type = ExcelImportType.getByDisplayName(importType);
            if (type == null) {
                throw new RuntimeException("不支持的导入类型: " + importType);
            }
            
            // 下载模板
            excelService.downloadTemplate(response, type.getDtoClass());
            
        } catch (Exception e) {
            log.error("下载Excel模板失败", e);
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的Excel导入类型
     * @return 支持的导入类型列表
     */
    @GetMapping("/supported-types")
    public Result<List<String>> getSupportedTypes() {
        try {
            List<String> types = List.of(ExcelImportType.getAllDisplayNames());
            return Result.success(types);
        } catch (Exception e) {
            log.error("获取支持的Excel类型失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

  
} 