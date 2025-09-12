package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.insightflow.entity.prod.DealRecord;
import com.insightflow.service.prod.DealRecordService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 成交记录Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SalesExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "销售单号", index = 0)
    private String salesOrderNo;

    @ExcelProperty(value = "销售时间", index = 1)
    @DateTimeFormat("yyyy-MM-dd HH:mm")
    private LocalDateTime salesDateTime;

    @ExcelProperty(value = "客户名称", index = 2)
    private String customerName;

    @ExcelProperty(value = "配送商", index = 3)
    private String distributor;

    @ExcelProperty(value = "配送商类", index = 4)
    private String distributorType;

    @ExcelProperty(value = "客户经理", index = 5)
    private String customerManager;

    @ExcelProperty(value = "业务员", index = 6)
    private String salesperson;

    @ExcelProperty(value = "商品名称", index = 7)
    private String productName;

    @ExcelProperty(value = "规格属性", index = 8)
    private String specification;

    @ExcelProperty(value = "销售数量", index = 9)
    private String salesQuantityStr;

    @ExcelProperty(value = "客户编码", index = 10)
    private String customerCode;

    @ExcelProperty(value = "客户类型", index = 11)
    private String customerType;

    @ExcelProperty(value = "是否赠品", index = 12)
    private String isGiftStr;

    @ExcelProperty(value = "销售单位", index = 13)
    private String salesUnit;

    @ExcelProperty(value = "业务员上级", index = 14)
    private String salespersonSupervisor;

    @ExcelProperty(value = "客户分类", index = 15)
    private String customerCategory;

    @ExcelProperty(value = "换算单位/行", index = 16)
    private String conversionUnit;

    @ExcelProperty(value = "商品系列", index = 17)
    private String productSeries;

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
        if (salesOrderNo == null || salesOrderNo.trim().isEmpty()) {
            return false;
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            return false;
        }
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }
        if (salesQuantityStr == null || salesQuantityStr.trim().isEmpty()) {
            return false;
        }
        
        // 验证销售数量（允许负数）
        BigDecimal quantity = parseSalesQuantity();
        return quantity != null;
    }

    @Override
    public Object toEntity() {
        DealRecord entity = new DealRecord();
        BeanUtils.copyProperties(this, entity);
        
        // 转换数字字段
        entity.setSalesQuantity(parseSalesQuantity());
        entity.setIsGift(parseIsGift());
        
        return entity;
    }

    /**
     * 解析销售数量 - 处理可能包含负数和非数字字符的情况
     */
    private BigDecimal parseSalesQuantity() {
        if (salesQuantityStr == null || salesQuantityStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 尝试直接解析（支持负数）
            return new BigDecimal(salesQuantityStr.trim());
        } catch (NumberFormatException e) {
            // 如果解析失败，尝试清理非数字字符，但保留负号
            try {
                String cleanStr = salesQuantityStr.trim().replaceAll("[^0-9.-]", "");
                if (cleanStr.isEmpty()) {
                    return null;
                }
                return new BigDecimal(cleanStr);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    /**
     * 解析是否赠品 - 处理多种文本格式
     */
    private Integer parseIsGift() {
        if (isGiftStr == null || isGiftStr.trim().isEmpty()) {
            return 0; // 默认不是赠品
        }
        String str = isGiftStr.trim().toLowerCase();
        if ("是".equals(str) || "1".equals(str) || "true".equals(str) || "yes".equals(str)) {
            return 1;
        } else if ("否".equals(str) || "0".equals(str) || "false".equals(str) || "no".equals(str)) {
            return 0;
        }
        return 0; // 默认不是赠品
    }

    @Override
    public Class<?> getServiceClass() {
        return DealRecordService.class;
    }
} 