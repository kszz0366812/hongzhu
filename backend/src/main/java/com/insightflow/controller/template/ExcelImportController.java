package com.insightflow.controller.template;

import com.insightflow.common.Result;
import com.insightflow.service.common.ExcelImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "Excel导入接口")
@RestController
@RequestMapping("/excel/import")
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @Operation(summary = "导入员工数据")
    @RequestMapping("/employee")
    public Result<Void> importEmployeeData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入员工数据");
        try {
            excelImportService.importEmployeeData(file);
            log.info("员工数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("员工数据导入失败", e);
            return Result.error("员工数据导入失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导入商品数据")
    @RequestMapping("/product")
    public Result<Void> importProductData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入商品数据");
        try {
            excelImportService.importProductData(file);
            log.info("商品数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("商品数据导入失败", e);
            return Result.error("商品数据导入失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导入终端商户数据")
    @RequestMapping("/terminal")
    public Result<Void> importTerminalData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入终端商户数据");
        try {
            excelImportService.importTerminalData(file);
            log.info("终端商户数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("终端商户数据导入失败", e);
            return Result.error("终端商户数据导入失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导入批发商数据")
    @RequestMapping("/wholesaler")
    public Result<Void> importWholesalerData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入批发商数据");
        try {
            excelImportService.importWholesalerData(file);
            log.info("批发商数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("批发商数据导入失败", e);
            return Result.error("批发商数据导入失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导入销售记录数据")
    @RequestMapping("/sales")
    public Result<Void> importSalesData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入销售记录数据");
        try {
            excelImportService.importSalesData(file);
            log.info("销售记录数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("销售记录数据导入失败", e);
            return Result.error("销售记录数据导入失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导入任务目标数据")
    @RequestMapping("/task-target")
    public Result<Void> importTaskTargetData(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        log.info("开始导入任务目标数据");
        try {
            excelImportService.importTaskTargetData(file);
            log.info("任务目标数据导入成功");
            return Result.success();
        } catch (Exception e) {
            log.error("任务目标数据导入失败", e);
            return Result.error("任务目标数据导入失败：" + e.getMessage());
        }
    }
} 