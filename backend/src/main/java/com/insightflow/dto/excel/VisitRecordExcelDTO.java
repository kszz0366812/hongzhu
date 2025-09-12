package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.service.customer.VisitRecordService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 拜访记录Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VisitRecordExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "拜访时间", index = 0)
    private String visitTimeStr;

    @ExcelProperty(value = "终端代码", index = 1)
    private String terminalCode;


    /**
     * 无参构造函数（EasyExcel需要）
     */
    public VisitRecordExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public VisitRecordExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
    }

    /**
     * 解析拜访时间字符串为LocalDate
     * @return LocalDate对象
     */
    private LocalDate parseVisitTime() {
        if (visitTimeStr == null || visitTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(visitTimeStr.trim(), formatter);
        } catch (Exception e) {
            return null;
        }
    }



    @Override
    public boolean isValid() {
        return
                visitTimeStr != null && !visitTimeStr.trim().isEmpty() && 
                terminalCode != null && !terminalCode.trim().isEmpty() &&
                parseVisitTime() != null;
    }

    @Override
    public Object toEntity() {
        VisitRecord entity = new VisitRecord();
        // 解析时间字符串并转换为LocalDateTime，默认时间为00:00:00
        LocalDate visitDate = parseVisitTime();
        if (visitDate != null) {
            entity.setVisitTime(visitDate.atStartOfDay());
        }
        entity.setTerminalCode(terminalCode);
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return VisitRecordService.class;
    }
} 