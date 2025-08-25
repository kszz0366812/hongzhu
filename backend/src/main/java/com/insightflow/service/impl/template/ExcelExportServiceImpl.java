package com.insightflow.service.impl.template;

import com.insightflow.common.config.ExcelTemplateConfig;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.mapper.prod.ProductInfoMapper;
import com.insightflow.mapper.prod.SalesRecordMapper;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.mapper.customer.WholesalerInfoMapper;
import com.insightflow.service.common.ExcelExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;
    
    @Autowired
    private ProductInfoMapper productInfoMapper;
    
    @Autowired
    private TerminalInfoMapper terminalInfoMapper;
    
    @Autowired
    private WholesalerInfoMapper wholesalerInfoMapper;

    @Autowired
    private SalesRecordMapper salesRecordMapper;

    @Autowired
    private ExcelTemplateConfig templateConfig;

    @Override
    public ByteArrayOutputStream exportEmployeeData(LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("员工数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"工号", "姓名", "状态", "所属大区", "所属地市", "所属区域", "岗位", "职级", "渠道", "入职日期"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 查询数据
            List<EmployeeInfo> employeeList = employeeInfoMapper.selectList(null);
            
            // 填充数据
            int rowNum = 1;
            for (EmployeeInfo employee : employeeList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(employee.getEmployeeCode());
                row.createCell(1).setCellValue(employee.getName());
                row.createCell(2).setCellValue(employee.getStatus());
                row.createCell(3).setCellValue(employee.getRegionLevel1());
                row.createCell(4).setCellValue(employee.getRegionLevel2());
                row.createCell(5).setCellValue(employee.getRegionLevel3());
                row.createCell(6).setCellValue(employee.getPosition());
                row.createCell(7).setCellValue(employee.getLevels());
                row.createCell(8).setCellValue(employee.getChannel());
                row.createCell(9).setCellValue(employee.getJoinDate().toString());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error("导出员工数据失败", e);
            throw new RuntimeException("导出员工数据失败", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportProductData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("商品数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"商品编码", "商品名称", "规格", "单价", "件价", "所属系列"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 查询数据
            List<ProductInfo> productList = productInfoMapper.selectList(null);
            
            // 填充数据
            int rowNum = 1;
            for (ProductInfo product : productList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getProductCode());
                row.createCell(1).setCellValue(product.getProductName());
                row.createCell(2).setCellValue(product.getSpecification());
                row.createCell(3).setCellValue(product.getUnitPrice().doubleValue());
                row.createCell(4).setCellValue(product.getCasePrice().doubleValue());
                row.createCell(5).setCellValue(product.getSeries());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error("导出商品数据失败", e);
            throw new RuntimeException("导出商品数据失败", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportTerminalData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("终端商户数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"终端编码", "终端名称", "类型", "标签", "客户经理", "是否排线"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 查询数据
            List<TerminalInfo> terminalList = terminalInfoMapper.selectList(null);
            
            // 填充数据
            int rowNum = 1;
            for (TerminalInfo terminal : terminalList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(terminal.getTerminalCode());
                row.createCell(1).setCellValue(terminal.getTerminalName());
                row.createCell(2).setCellValue(terminal.getTerminalType());
                row.createCell(3).setCellValue(terminal.getTags());
                row.createCell(4).setCellValue(terminal.getCustomerManager());
                row.createCell(5).setCellValue(terminal.getIsScheduled());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error("导出终端商户数据失败", e);
            throw new RuntimeException("导出终端商户数据失败", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportWholesalerData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("批发商数据");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"经销商编码", "经销商名称", "等级", "联系人", "联系电话", "客户经理"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 查询数据
            List<WholesalerInfo> wholesalerList = wholesalerInfoMapper.selectList(null);
            
            // 填充数据
            int rowNum = 1;
            for (WholesalerInfo wholesaler : wholesalerList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(wholesaler.getDealerCode());
                row.createCell(1).setCellValue(wholesaler.getDealerName());
                row.createCell(2).setCellValue(wholesaler.getLevel());
                row.createCell(3).setCellValue(wholesaler.getContactPerson());
                row.createCell(4).setCellValue(wholesaler.getContactPhone());
                row.createCell(5).setCellValue(wholesaler.getCustomerManager());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error("导出批发商数据失败", e);
            throw new RuntimeException("导出批发商数据失败", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportByTemplate(String templateCode, Map<String, Object> params) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 获取模板定义
            ExcelTemplateConfig.TemplateDefinition template = templateConfig.getTemplate(templateCode);
            if (template == null) {
                throw new RuntimeException("模板不存在：" + templateCode);
            }

            // 为每个sheet创建数据
            for (ExcelTemplateConfig.SheetDefinition sheetDef : template.getSheets()) {
                Sheet sheet = workbook.createSheet(sheetDef.getTitle());
                
                // 创建表头
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < sheetDef.getColumns().size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(sheetDef.getColumns().get(i).getTitle());
                }

                // 获取数据源
                List<Map<String, Object>> dataList = getDataFromSource(sheetDef.getDataSource(), params);
                
                // 填充数据
                int rowNum = 1;
                for (Map<String, Object> data : dataList) {
                    Row row = sheet.createRow(rowNum++);
                    for (int i = 0; i < sheetDef.getColumns().size(); i++) {
                        ExcelTemplateConfig.ColumnDefinition column = sheetDef.getColumns().get(i);
                        Cell cell = row.createCell(i);
                        Object value = data.get(column.getField());
                        setCellValue(cell, value, column.getType());
                    }
                }

                // 自动调整列宽
                for (int i = 0; i < sheetDef.getColumns().size(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (Exception e) {
            log.error("模板导出失败: {}", templateCode, e);
            throw new RuntimeException("模板导出失败：" + e.getMessage());
        }
    }

    private List<Map<String, Object>> getDataFromSource(String dataSource, Map<String, Object> params) {
        LocalDate startDate = (LocalDate) params.get("startDate");
        LocalDate endDate = (LocalDate) params.get("endDate");
        String regionCode = (String) params.get("regionCode");
        String productCode = (String) params.get("productCode");

        switch (dataSource) {
            case "salesRecordService":
                return getSalesRecordData(startDate, endDate, regionCode, productCode);
            case "productService":
                return getProductAnalysisData(startDate, endDate, regionCode);
            case "regionService":
                return getRegionAnalysisData(startDate, endDate);
            default:
                throw new RuntimeException("未知的数据源：" + dataSource);
        }
    }

    private List<Map<String, Object>> getSalesRecordData(LocalDate startDate, LocalDate endDate, 
            String regionCode, String productCode) {
        // 构建查询条件
        Map<String, Object> queryParams = new HashMap<>();
        if (startDate != null) {
            queryParams.put("startDate", startDate);
        }
        if (endDate != null) {
            queryParams.put("endDate", endDate);
        }
        if (regionCode != null) {
            queryParams.put("regionCode", regionCode);
        }
        if (productCode != null) {
            queryParams.put("productCode", productCode);
        }

        // 查询销售记录
        List<SalesRecord> records = salesRecordMapper.selectByParams(queryParams);
        
        // 转换为Map格式
        return records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("salesDate", record.getSalesDate());
            map.put("customerName", record.getCustomerName());
            map.put("productName", record.getProductName());
            map.put("quantity", record.getQuantity());
            map.put("totalPrice", record.getTotalPrice());
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getProductAnalysisData(LocalDate startDate, LocalDate endDate, 
            String regionCode) {
        // 构建查询条件
        Map<String, Object> queryParams = new HashMap<>();
        if (startDate != null) {
            queryParams.put("startDate", startDate);
        }
        if (endDate != null) {
            queryParams.put("endDate", endDate);
        }
        if (regionCode != null) {
            queryParams.put("regionCode", regionCode);
        }

        // 查询商品分析数据
        List<Map<String, Object>> analysisData = salesRecordMapper.selectProductAnalysis(queryParams);
        return analysisData;
    }

    private List<Map<String, Object>> getRegionAnalysisData(LocalDate startDate, LocalDate endDate) {
        // 构建查询条件
        Map<String, Object> queryParams = new HashMap<>();
        if (startDate != null) {
            queryParams.put("startDate", startDate);
        }
        if (endDate != null) {
            queryParams.put("endDate", endDate);
        }

        // 查询区域分析数据
        List<Map<String, Object>> analysisData = salesRecordMapper.selectRegionAnalysis(queryParams);
        return analysisData;
    }

    private void setCellValue(Cell cell, Object value, String type) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        switch (type) {
            case "date":
                if (value instanceof LocalDate) {
                    cell.setCellValue(((LocalDate) value).toString());
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "number":
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "percentage":
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue() / 100);
                    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
                    style.setDataFormat(cell.getSheet().getWorkbook().createDataFormat().getFormat("0.00%"));
                    cell.setCellStyle(style);
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            default:
                cell.setCellValue(value.toString());
        }
    }
} 