package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.service.prod.SalesRecordService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 销售记录Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SalesExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "销售订单号", index = 0)
    private String salesOrderNo;

    @ExcelProperty(value = "销售日期", index = 1)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime salesDate;

    @ExcelProperty(value = "客户名称", index = 2)
    private String customerName;

    @ExcelProperty(value = "客户级别", index = 3)
    private String customerLevel;

    @ExcelProperty(value = "经销商", index = 4)
    private String distributor;

    @ExcelProperty(value = "客户经理", index = 5)
    private String customerManager;

    @ExcelProperty(value = "销售人员", index = 6)
    private String salesperson;

    @ExcelProperty(value = "产品ID", index = 7)
    private Long productId;

    @ExcelProperty(value = "数量", index = 8)
    private Integer quantity;

    @ExcelProperty(value = "是否赠品", index = 9)
    private Integer isGift;

    @ExcelProperty(value = "单位", index = 10)
    private String unit;

    @ExcelProperty(value = "单价", index = 11)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "总价", index = 12)
    private BigDecimal totalPrice;

    /**
     * 无参构造函数（EasyExcel需要）
     */
    public SalesExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public SalesExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
    }

    @Override
    public boolean isValid() {
        return salesOrderNo != null && !salesOrderNo.trim().isEmpty() && 
               customerName != null && !customerName.trim().isEmpty();
    }

    @Override
    public Object toEntity() {
        SalesRecord entity = new SalesRecord();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return SalesRecordService.class;
    }
} 