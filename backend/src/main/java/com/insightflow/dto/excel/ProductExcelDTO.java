package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.service.prod.ProductService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 产品Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "产品编码", index = 0)
    private String productCode;

    @ExcelProperty(value = "产品名称", index = 1)
    private String productName;

    @ExcelProperty(value = "规格", index = 2)
    private String specification;

    @ExcelProperty(value = "单价", index = 3)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "箱价", index = 4)
    private BigDecimal casePrice;

    @ExcelProperty(value = "系列", index = 5)
    private String series;

    // 添加无参构造函数，EasyExcel实例化对象需要
    public ProductExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public ProductExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
    }

    @Override
    public boolean isValid() {
        return productCode != null && !productCode.trim().isEmpty() && 
               productName != null && !productName.trim().isEmpty();
    }

    @Override
    public Object toEntity() {
        ProductInfo entity = new ProductInfo();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return ProductService.class;
    }
} 