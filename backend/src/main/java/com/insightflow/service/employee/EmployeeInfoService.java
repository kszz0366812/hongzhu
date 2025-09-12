package com.insightflow.service.employee;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
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

    /**
     * 分页查询员工列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 搜索关键词（支持员工编号、姓名、职位、区域、职级等字段的模糊搜索）
     * @param status  状态
     * @return 分页结果
     */
    PageInfo<EmployeeInfo> getEmployeePage(Integer page, Integer size, String keyword, Integer status);
}