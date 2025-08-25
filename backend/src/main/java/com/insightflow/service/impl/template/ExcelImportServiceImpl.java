package com.insightflow.service.impl.template;

import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.mapper.customer.WholesalerInfoMapper;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.mapper.prod.ProductInfoMapper;
import com.insightflow.mapper.prod.SalesRecordMapper;
import com.insightflow.mapper.task.TaskTargetMapper;
import com.insightflow.service.common.ExcelImportService;
import com.insightflow.common.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

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
    private TaskTargetMapper taskTargetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importEmployeeData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<EmployeeInfo> employeeList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                EmployeeInfo employee = new EmployeeInfo();
                employee.setEmployeeCode(ExcelUtil.getCellValue(row.getCell(0)));
                employee.setName(ExcelUtil.getCellValue(row.getCell(1)));
                employee.setStatus(Integer.parseInt(ExcelUtil.getCellValue(row.getCell(2))));
                employee.setRegionLevel1(ExcelUtil.getCellValue(row.getCell(3)));
                employee.setRegionLevel2(ExcelUtil.getCellValue(row.getCell(4)));
                employee.setRegionLevel3(ExcelUtil.getCellValue(row.getCell(5)));
                employee.setResponsibleRegions(ExcelUtil.getCellValue(row.getCell(6)));
                employee.setDirectLeaderId(Long.parseLong(ExcelUtil.getCellValue(row.getCell(7))));
                employee.setPosition(ExcelUtil.getCellValue(row.getCell(8)));
                employee.setLevels(ExcelUtil.getCellValue(row.getCell(9)));
                employee.setChannel(ExcelUtil.getCellValue(row.getCell(10)));
                employee.setJoinDate(LocalDate.parse(ExcelUtil.getCellValue(row.getCell(11)), DateTimeFormatter.ISO_DATE));

                employeeList.add(employee);
            }

            for (EmployeeInfo employee : employeeList) {
                employeeInfoMapper.insertOrUpdate(employee);
            }
        } catch (Exception e) {
            log.error("导入员工数据失败", e);
            throw new RuntimeException("导入员工数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importProductData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<ProductInfo> productList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ProductInfo product = new ProductInfo();
                product.setProductCode(ExcelUtil.getCellValue(row.getCell(0)));
                product.setProductName(ExcelUtil.getCellValue(row.getCell(1)));
                product.setSpecification(ExcelUtil.getCellValue(row.getCell(2)));
                product.setUnitPrice(new java.math.BigDecimal(ExcelUtil.getCellValue(row.getCell(3))));
                product.setCasePrice(new java.math.BigDecimal(ExcelUtil.getCellValue(row.getCell(4))));
                product.setSeries(ExcelUtil.getCellValue(row.getCell(5)));

                productList.add(product);
            }

            for (ProductInfo product : productList) {
                productInfoMapper.insertOrUpdate(product);
            }
        } catch (Exception e) {
            log.error("导入商品数据失败", e);
            throw new RuntimeException("导入商品数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importTerminalData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<TerminalInfo> terminalList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                TerminalInfo terminal = new TerminalInfo();
                terminal.setTerminalCode(ExcelUtil.getCellValue(row.getCell(0)));
                terminal.setTerminalName(ExcelUtil.getCellValue(row.getCell(1)));
                terminal.setTerminalType(ExcelUtil.getCellValue(row.getCell(2)));
                terminal.setTags(ExcelUtil.getCellValue(row.getCell(3)));
                terminal.setCustomerManager(ExcelUtil.getCellValue(row.getCell(4)));
                terminal.setIsScheduled(Integer.parseInt(ExcelUtil.getCellValue(row.getCell(5))));

                terminalList.add(terminal);
            }

            for (TerminalInfo terminal : terminalList) {
                terminalInfoMapper.insertOrUpdate(terminal);
            }
        } catch (Exception e) {
            log.error("导入终端商户数据失败", e);
            throw new RuntimeException("导入终端商户数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importWholesalerData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<WholesalerInfo> wholesalerList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                WholesalerInfo wholesaler = new WholesalerInfo();
                wholesaler.setDealerCode(ExcelUtil.getCellValue(row.getCell(0)));
                wholesaler.setDealerName(ExcelUtil.getCellValue(row.getCell(1)));
                wholesaler.setLevel(ExcelUtil.getCellValue(row.getCell(2)));
                wholesaler.setContactPerson(ExcelUtil.getCellValue(row.getCell(3)));
                wholesaler.setContactPhone(ExcelUtil.getCellValue(row.getCell(4)));
                wholesaler.setCustomerManager(ExcelUtil.getCellValue(row.getCell(5)));

                wholesalerList.add(wholesaler);
            }

            for (WholesalerInfo wholesaler : wholesalerList) {
                wholesalerInfoMapper.insertOrUpdate(wholesaler);
            }
        } catch (Exception e) {
            log.error("导入批发商数据失败", e);
            throw new RuntimeException("导入批发商数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importSalesData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<SalesRecord> salesList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                SalesRecord sales = new SalesRecord();
                sales.setSalesOrderNo(ExcelUtil.getCellValue(row.getCell(0)));
                sales.setSalesDate(LocalDateTime.parse(ExcelUtil.getCellValue(row.getCell(1)), DateTimeFormatter.ISO_DATE_TIME));
                sales.setCustomerName(ExcelUtil.getCellValue(row.getCell(2)));
                sales.setCustomerLevel(ExcelUtil.getCellValue(row.getCell(3)));
                sales.setDistributor(ExcelUtil.getCellValue(row.getCell(4)));
                sales.setCustomerManager(ExcelUtil.getCellValue(row.getCell(5)));
                sales.setSalesperson(ExcelUtil.getCellValue(row.getCell(6)));
                sales.setProductId(Long.parseLong(ExcelUtil.getCellValue(row.getCell(7))));
                sales.setQuantity(Integer.parseInt(ExcelUtil.getCellValue(row.getCell(8))));
                sales.setIsGift(Integer.parseInt(ExcelUtil.getCellValue(row.getCell(9))));
                sales.setUnit(ExcelUtil.getCellValue(row.getCell(10)));
                sales.setUnitPrice(new java.math.BigDecimal(ExcelUtil.getCellValue(row.getCell(11))));
                sales.setTotalPrice(new java.math.BigDecimal(ExcelUtil.getCellValue(row.getCell(12))));

                salesList.add(sales);
            }

            for (SalesRecord sales : salesList) {
                salesRecordMapper.insertOrUpdate(sales);
            }
        } catch (Exception e) {
            log.error("导入销售记录数据失败", e);
            throw new RuntimeException("导入销售记录数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importTaskTargetData(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<TaskTarget> taskList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                TaskTarget task = new TaskTarget();
                task.setExecutorId(Long.parseLong(ExcelUtil.getCellValue(row.getCell(0))));
                task.setStartTime(LocalDateTime.parse(ExcelUtil.getCellValue(row.getCell(1)), DateTimeFormatter.ISO_DATE_TIME));
                task.setEndTime(LocalDateTime.parse(ExcelUtil.getCellValue(row.getCell(2)), DateTimeFormatter.ISO_DATE_TIME));
                task.setTaskName(ExcelUtil.getCellValue(row.getCell(3)));
                task.setTargetAmount(new BigDecimal(ExcelUtil.getCellValue(row.getCell(4))));
                task.setAchievedAmount(new BigDecimal(ExcelUtil.getCellValue(row.getCell(5))));

                taskList.add(task);
            }

            for (TaskTarget task : taskList) {
                taskTargetMapper.insertOrUpdate(task);
            }
        } catch (Exception e) {
            log.error("导入任务目标数据失败", e);
            throw new RuntimeException("导入任务目标数据失败: " + e.getMessage());
        }
    }
} 