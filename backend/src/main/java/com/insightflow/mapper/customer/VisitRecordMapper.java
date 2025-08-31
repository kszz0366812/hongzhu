package com.insightflow.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.dto.EmployeeVisitRateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecord> {
    List<EmployeeVisitRateDTO> getEmployeeVisitRate(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
} 