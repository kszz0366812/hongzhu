package com.insightflow.common.enums;

import com.insightflow.dto.excel.ExcelDTO;
import com.insightflow.dto.excel.EmployeeExcelDTO;
import com.insightflow.dto.excel.ProductExcelDTO;
import com.insightflow.dto.excel.TerminalExcelDTO;
import com.insightflow.dto.excel.WholesalerExcelDTO;
import com.insightflow.dto.excel.SalesExcelDTO;
import com.insightflow.dto.excel.TaskTargetExcelDTO;
import com.insightflow.dto.excel.VisitRecordExcelDTO;

/**
 * Excel导入类型枚举
 * 用于映射导入类型到对应的DTO类
 */
public enum ExcelImportType {
    
    EMPLOYEE("员工导入", "员工信息", EmployeeExcelDTO.class),
    PRODUCT("产品导入", "产品信息", ProductExcelDTO.class),
    TERMINAL("终端商户导入", "终端商户信息", TerminalExcelDTO.class),
    WHOLESALER("批发商导入", "批发商信息", WholesalerExcelDTO.class),
    SALES("成交记录导入", "成交记录", SalesExcelDTO.class),
    TASK_TARGET("任务目标导入", "任务目标", TaskTargetExcelDTO.class),
    VISIT_RECORD("拜访记录导入", "拜访记录", VisitRecordExcelDTO.class);

    private final String displayName;      // 显示名称
    private final String fileName;         // Excel文件名
    private final Class<? extends ExcelDTO> dtoClass; // 对应的DTO类

    ExcelImportType(String displayName, String fileName, Class<? extends ExcelDTO> dtoClass) {
        this.displayName = displayName;
        this.fileName = fileName;
        this.dtoClass = dtoClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public Class<? extends ExcelDTO> getDtoClass() {
        return dtoClass;
    }

    /**
     * 根据显示名称获取导入类型
     * @param displayName 显示名称
     * @return 导入类型，如果未找到返回null
     */
    public static ExcelImportType getByDisplayName(String displayName) {
        for (ExcelImportType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取所有显示名称
     * @return 显示名称数组
     */
    public static String[] getAllDisplayNames() {
        ExcelImportType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].displayName;
        }
        return names;
    }
} 