package com.insightflow.dto.excel;

import lombok.Data;

/**
 * Excel DTO基类
 * 所有Excel导入导出的DTO都应该继承此类
 * 具体的文件名、Sheet名称、表名等信息通过ExcelImportType枚举管理
 */
@Data
public abstract class ExcelDTO {
    
    /**
     * 验证数据有效性
     * @return true表示数据有效，false表示数据无效
     */
    public abstract boolean isValid();
    
    /**
     * 转换为对应的Entity对象
     * @return 转换后的Entity对象
     */
    public abstract Object toEntity();
    
    /**
     * 获取对应的Service类
     * @return Service类的Class对象
     */
    public abstract Class<?> getServiceClass();
 
} 