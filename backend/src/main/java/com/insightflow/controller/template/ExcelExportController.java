package com.insightflow.controller.template;

import com.insightflow.common.config.ExcelTemplateConfig;
import com.insightflow.service.common.ExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Excel导出接口")
@RestController
@RequestMapping("/excel/export")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ExcelTemplateConfig templateConfig;

    @Operation(summary = "获取可用的导出模板列表")
    @RequestMapping("/templates")
    public List<Map<String, String>> getTemplates() {
        return templateConfig.getTemplateMap().values().stream()
                .map(template -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", template.getCode());
                    map.put("name", template.getName());
                    map.put("description", template.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "导出员工数据")
    @RequestMapping("/employee")
    public ResponseEntity<byte[]> exportEmployeeData(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("开始导出员工数据 - 开始日期: {}, 结束日期: {}", startDate, endDate);
        try {
            ByteArrayOutputStream outputStream = excelExportService.exportEmployeeData(startDate, endDate);
            log.info("员工数据导出成功");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "employee_data.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("员工数据导出失败", e);
            throw new RuntimeException("员工数据导出失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出商品数据")
    @RequestMapping("/product")
    public ResponseEntity<byte[]> exportProductData() {
        log.info("开始导出商品数据");
        try {
            ByteArrayOutputStream outputStream = excelExportService.exportProductData();
            log.info("商品数据导出成功");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "product_data.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("商品数据导出失败", e);
            throw new RuntimeException("商品数据导出失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出终端商户数据")
    @RequestMapping("/terminal")
    public ResponseEntity<byte[]> exportTerminalData() {
        log.info("开始导出终端商户数据");
        try {
            ByteArrayOutputStream outputStream = excelExportService.exportTerminalData();
            log.info("终端商户数据导出成功");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "terminal_data.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("终端商户数据导出失败", e);
            throw new RuntimeException("终端商户数据导出失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出批发商数据")
    @RequestMapping("/wholesaler")
    public ResponseEntity<byte[]> exportWholesalerData() {
        log.info("开始导出批发商数据");
        try {
            ByteArrayOutputStream outputStream = excelExportService.exportWholesalerData();
            log.info("批发商数据导出成功");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "wholesaler_data.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("批发商数据导出失败", e);
            throw new RuntimeException("批发商数据导出失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据模板导出数据")
    @RequestMapping("/template/{templateCode}")
    public ResponseEntity<byte[]> exportByTemplate(
            @Parameter(description = "模板编码") @PathVariable String templateCode,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "区域编码") @RequestParam(required = false) String regionCode,
            @Parameter(description = "商品编码") @RequestParam(required = false) String productCode) {
        log.info("开始导出模板数据 - 模板: {}, 开始日期: {}, 结束日期: {}, 区域: {}, 商品: {}", 
                templateCode, startDate, endDate, regionCode, productCode);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("regionCode", regionCode);
            params.put("productCode", productCode);
            
            ByteArrayOutputStream outputStream = excelExportService.exportByTemplate(templateCode, params);
            log.info("模板数据导出成功");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", templateCode + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("模板数据导出失败", e);
            throw new RuntimeException("模板数据导出失败：" + e.getMessage());
        }
    }
} 