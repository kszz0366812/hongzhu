package com.insightflow.service.common;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {
    /**
     * 导入员工数据
     */
    void importEmployeeData(MultipartFile file);

    /**
     * 导入商品数据
     */
    void importProductData(MultipartFile file);

    /**
     * 导入终端商户数据
     */
    void importTerminalData(MultipartFile file);

    /**
     * 导入批发商数据
     */
    void importWholesalerData(MultipartFile file);

    /**
     * 导入销售记录数据
     */
    void importSalesData(MultipartFile file);

    /**
     * 导入任务目标数据
     */
    void importTaskTargetData(MultipartFile file);
} 