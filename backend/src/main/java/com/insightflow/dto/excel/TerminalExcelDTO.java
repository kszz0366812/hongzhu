package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.service.customer.TerminalInfoService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * 终端商户Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TerminalExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "终端名称", index = 0)
    private String terminalName;

    @ExcelProperty(value = "终端编码", index = 1)
    private String terminalCode;

    @ExcelProperty(value = "客户经理", index = 2)
    private String customerManager;

    @ExcelProperty(value = "标签", index = 3)
    private String tags;

    @ExcelProperty(value = "终端类型", index = 4)
    private String terminalType;

    @ExcelProperty(value = "是否排线", index = 5)
    private String isScheduled;

    /**
     * 无参构造函数（EasyExcel需要）
     */
    public TerminalExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public TerminalExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
        isScheduled = map.get("isScheduled").equals(0) ? "否" : "是";
    }

    @Override
    public boolean isValid() {
        return terminalCode != null && !terminalCode.trim().isEmpty() && 
               terminalName != null && !terminalName.trim().isEmpty();
    }

    @Override
    public Object toEntity() {
        TerminalInfo entity = new TerminalInfo();
        BeanUtils.copyProperties(this, entity);
        entity.setIsScheduled(isScheduled.equals("是") ? 1 : 0);
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return TerminalInfoService.class;
    }
} 