package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.insightflow.entity.task.TaskTarget;
import com.insightflow.service.task.TaskTargetService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务目标Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskTargetExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "执行人", index = 0)
    private String executor;

    @ExcelProperty(value = "开始时间", index = 1)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ExcelProperty(value = "结束时间", index = 2)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ExcelProperty(value = "任务名称", index = 3)
    private String taskName;

    @ExcelProperty(value = "目标金额", index = 4)
    private BigDecimal targetAmount;

    @ExcelProperty(value = "已完成金额", index = 5)
    private BigDecimal achievedAmount;

    /**
     * 无参构造函数（EasyExcel需要）
     */
    public TaskTargetExcelDTO() {
    }

    /**
     * 从Map构造DTO的构造函数
     * @param map 源数据Map
     */
    public TaskTargetExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
    }

    @Override
    public boolean isValid() {
        return executor != null ;
    }

    @Override
    public Object toEntity() {
        TaskTarget entity = new TaskTarget();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    @Override
    public Class<?> getServiceClass() {
        return TaskTargetService.class;
    }
} 