package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.service.customer.WholesalerService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * 批发商Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WholesalerExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "经销商编码", index = 0)
    private String dealerCode;

    @ExcelProperty(value = "经销商名称", index = 1)
    private String dealerName;

    @ExcelProperty(value = "客户经理", index = 2)
    private String customerManager;

    /**
     * 无参构造函数（EasyExcel需要）
     */
    public WholesalerExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public WholesalerExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
    }

    @Override
    public boolean isValid() {
        return dealerCode != null && !dealerCode.trim().isEmpty() && 
               dealerName != null && !dealerName.trim().isEmpty();
    }

    @Override
    public Object toEntity() {
        WholesalerInfo entity = new WholesalerInfo();
        BeanUtils.copyProperties(this, entity);
        entity.setLevel("二批商");
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return WholesalerService.class;
    }
} 