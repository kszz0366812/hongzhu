package com.insightflow.service.employee;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.employee.EmployeeInfo;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeInfoService extends IService<EmployeeInfo> {
    List<EmployeeInfo> getByRegion(String regionLevel1, String regionLevel2, String regionLevel3);
    List<EmployeeInfo> getByPosition(String position);
    List<EmployeeInfo> getByChannel(String channel);
    List<EmployeeInfo> getByDirectLeader(Long leaderId);
    boolean updateStatus(Long id, Integer status);
    List<EmployeeInfo> getByJoinDateRange(LocalDate startDate, LocalDate endDate);
    List<EmployeeInfo> getByRank(String rank);
    List<EmployeeInfo> searchEmployees(String keyword);
} 